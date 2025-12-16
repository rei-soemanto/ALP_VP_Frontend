package com.example.alp_vp_frontend.data.container

import com.example.alp_vp_frontend.data.repository.AuthRepository
import com.example.alp_vp_frontend.data.repository.PostRepository
import com.example.alp_vp_frontend.data.repository.UserRepository
import com.example.alp_vp_frontend.data.service.ApiService
import com.example.alp_vp_frontend.data.service.PostApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
        private val BASE_URL = "http://10.0.2.2:3000/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val postApiService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(retrofitService)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(retrofitService)
    }

    val postRepository: PostRepository by lazy {
        PostRepository(postApiService)
    }
}