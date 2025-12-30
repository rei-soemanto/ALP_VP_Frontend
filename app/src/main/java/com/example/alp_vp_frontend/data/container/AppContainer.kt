package com.example.alp_vp_frontend.data.container

import android.content.Context
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.repository.AuthRepository
import com.example.alp_vp_frontend.data.repository.ChatRepository
import com.example.alp_vp_frontend.data.repository.PostRepository
import com.example.alp_vp_frontend.data.repository.UserRepository
import com.example.alp_vp_frontend.data.service.ApiService
import com.example.alp_vp_frontend.data.service.ChatApiService
import com.example.alp_vp_frontend.data.service.PostApiService
import io.socket.client.IO
import io.socket.client.Socket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(appContext: Context) {
    private val BASE_URL = "http://10.0.2.2:3000"
    private val API_BASE_URL = "$BASE_URL/api/"

    fun createSocket(token: String): Socket {
        val options = IO.Options().apply {
            transports = arrayOf("websocket")
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 1_000
            reconnectionDelayMax = 5_000
            timeout = 10_000
            query = "token=$token"
        }

        return IO.socket(BASE_URL, options)
    }

    private val dataStoreManager = DataStoreManager(appContext)

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(API_BASE_URL)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val postApiService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }

    val chatApiService: ChatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(retrofitService, dataStoreManager)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(retrofitService, dataStoreManager)
    }

    val postRepository: PostRepository by lazy {
        PostRepository(postApiService, dataStoreManager)
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepository(chatApiService, dataStoreManager, ::createSocket)
    }
}