package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.AddInterestRequest
import com.example.alp_vp_frontend.data.dto.ApiResponse
import com.example.alp_vp_frontend.data.dto.InterestResponse
import com.example.alp_vp_frontend.data.dto.LoginRequest
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.dto.RegisterRequest
import com.example.alp_vp_frontend.data.dto.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<UserResponse>

    @GET("interests")
    suspend fun getInterests(): ApiResponse<List<InterestResponse>>

    @POST("users/interests")
    suspend fun addUserInterests(
        @Header("Authorization") token: String,
        @Body request: List<AddInterestRequest>
    ): ApiResponse<String>

    @GET("users/current")
    suspend fun getCurrentUser(@Header("Authorization") token: String): ApiResponse<UserResponse>

    @Multipart
    @PATCH("users/current")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Part("fullName") fullName: RequestBody,
        @Part("about") about: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): ApiResponse<UserResponse>

    @GET("posts/mine")
    suspend fun getMyPosts(@Header("Authorization") token: String): ApiResponse<List<PostResponse>>
}