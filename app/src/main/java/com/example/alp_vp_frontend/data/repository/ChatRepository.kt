package com.example.alp_vp_frontend.data.repository;

import android.R.id.message
import android.content.Context
import android.net.Uri
import android.util.Log.e
import android.webkit.MimeTypeMap
import androidx.compose.ui.platform.LocalContext
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.example.alp_vp_frontend.data.dto.ListMessageRequest
import com.example.alp_vp_frontend.data.local.DataStoreManager;
import com.example.alp_vp_frontend.data.mapper.ChatMessageMapper
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import com.example.alp_vp_frontend.data.service.ChatApiService;
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File

class ChatRepository(
    private val chatApiService: ChatApiService,
    private val datastore: DataStoreManager,
    private val createSocket: (token: String) -> Socket
) {
    private var socket: Socket? = null
    private var activeCounterPartId: Int? = null
    private val _incomingMessages = MutableSharedFlow<ChatMessage>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val _onReconnect = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onReconnect = _onReconnect.asSharedFlow()

    private val _onRead = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onRead = _onRead.asSharedFlow()

    private val socketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private suspend fun getAuthHeader(): String {
        val token = datastore.tokenFlow.first() ?: ""
        return "Bearer $token"
    }

    private fun prepareImageParts(
        context: Context,
        uris: List<Uri>
    ): List<MultipartBody.Part> {

        val resolver = context.contentResolver

        return uris.mapIndexedNotNull { index, uri ->
            try {
                val mimeType = resolver.getType(uri) ?: return@mapIndexedNotNull null
                if (!mimeType.startsWith("image/")) return@mapIndexedNotNull null

                val extension = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType)
                    ?: "bin"

                val file = File(
                    context.cacheDir,
                    "image_${index}_${System.currentTimeMillis()}.$extension"
                )

                resolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                MultipartBody.Part.createFormData(
                    name = "images",
                    filename = file.name,
                    body = file.asRequestBody(mimeType.toMediaType())
                )

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getChatList(): List<ChatListItem> {
        try {
            val response = chatApiService.getChatList(getAuthHeader()).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun getMessages(counterPartId: Int): List<ChatMessage> {
        try {
            val response = chatApiService.getMessages(getAuthHeader(), counterPartId).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun sendMessage(context: Context, counterPartId: Int, message: String, imageURIs: List<Uri>) {
        try {
            val imageParts = prepareImageParts(context, imageURIs)
            val messagePart = message.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = chatApiService.sendMessage(getAuthHeader(), counterPartId, messagePart, imageParts)
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun readMessage(messageId: Int) {
        try {
            val response = chatApiService.readMessage(getAuthHeader(), messageId)
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun getImages(messageId: Int): List<String> {
        try {
            val response = chatApiService.getImages(getAuthHeader(), messageId).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    // Socket.io
    suspend fun connect(counterPartId: Int) {
        if (activeCounterPartId == counterPartId && socket?.connected() == true) {
            return
        }

        disconnect()

        val sock = createSocket(getAuthHeader())
        socket = sock
        activeCounterPartId = counterPartId

        sock.on("message") { args ->
            val message = ChatMessageMapper.fromSocketArgs(args)

            if (message != null) {
                _incomingMessages.tryEmit(message)

//                println("Sender: " + message.senderId)
//                println("Counterpart: " + counterPartId)

                if (message.senderId == counterPartId) {
                    socketScope.launch {
                        readMessage(message.id)
                    }
                }
            }
        }

        sock.on("read") { args ->
            _onRead.tryEmit(Unit)
        }

        sock.on(Socket.EVENT_CONNECT) {
            sock.emit("counterPartId", counterPartId)
            _onReconnect.tryEmit(Unit)
        }

        sock.connect()
    }

    suspend fun disconnect() {
        println("Disconnect from socket")
        socket?.off()
        socket?.disconnect()
        socket?.close()
        socket = null

        socketScope.coroutineContext.cancelChildren()
    }
}
