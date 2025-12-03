package com.example.alp_vp_frontend.data.container

import com.example.alp_vp_frontend.data.repository.AuthRepository
import com.example.alp_vp_frontend.data.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val authRepository: AuthRepository
}

class DefaultAppContainer : AppContainer {

    // IMPORTANT: 10.0.2.2 is the special IP for Android Emulator to access your computer's localhost
    private val BASE_URL = "http://10.0.2.2:3000/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(retrofitService)
    }
}