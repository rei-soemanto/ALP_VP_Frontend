package com.example.alp_vp_frontend.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log.e
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.example.alp_vp_frontend.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private var _listenersRunning = false

    fun getMessages(counterPartId: Int) {
        viewModelScope.launch {
            try {
                _messages.value = chatRepository.getMessages(counterPartId)
            } catch (e: Exception) {
                // handle error
                println(e)
            }
        }
    }

    fun sendMessage(context: Context, counterPartId: Int, content: String, imageURIs: List<Uri>) {
        if (content.isBlank() && imageURIs.isEmpty()) return

        viewModelScope.launch {
            try {
                chatRepository.sendMessage(context, counterPartId, content, imageURIs)
            } catch (e: Exception) {
                // handle error
                println(e)
            }
        }
    }

    // Socket.io
    fun connectSocket(counterPartId: Int) {
        println("ChatViewModel connect socket")

        viewModelScope.launch {
            chatRepository.connect(counterPartId)
        }
    }

    fun disconnectSocket() {
        println("ChatviewModel disconnect socket")

        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }

    fun listenToIncomingMessages() {
        viewModelScope.launch {
            chatRepository.incomingMessages
                .collect {
                    message -> _messages.update { listOf(message) + it }
                }
        }
    }

    fun listenToMessageRead() {
        viewModelScope.launch {
            chatRepository.onRead.collect {
                _messages.update { it.map { msg -> msg.copy(read = true) } }
            }
        }
    }

    fun listenToReconnects(counterPartId: Int) {
        viewModelScope.launch {
            chatRepository.onReconnect.collect {
                try {
                    _messages.value = chatRepository.getMessages(counterPartId)
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }

    fun runListeners(counterPartId: Int) {
        if (_listenersRunning) return
        _listenersRunning = true

        listenToIncomingMessages()
        listenToMessageRead()
        listenToReconnects(counterPartId)
        connectSocket(counterPartId)
    }

//    override fun onCleared() {
//        disconnectSocket()
//    }
}