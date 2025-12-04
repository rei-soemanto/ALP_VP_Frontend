package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PostApiService {
    @GET("api/posts")
    suspend fun getAllPosts(
        @Header("Authorization") token: String
    ): List<PostResponse>

    @GET("api/users/current/posts")
    suspend fun getUserPosts(
        @Header("Authorization") token: String
    ): List<PostResponse>

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("caption") caption: RequestBody,
        @Part("is_public") isPublic: RequestBody,
        @Part image: MultipartBody.Part
    ): PostResponse

    @Multipart
    @POST("api/posts/{id}")
    suspend fun updatePost(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("caption") caption: RequestBody,
        @Part("is_public") isPublic: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): PostResponse

    @DELETE("api/posts/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )
}