package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.kt.ApiResponse
import com.example.alp_vp_frontend.data.kt.CommentResponse
import com.example.alp_vp_frontend.data.kt.CreateCommentRequest
import com.example.alp_vp_frontend.data.kt.PostResponse
import com.example.alp_vp_frontend.data.kt.UpdatePostRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PostApiService {
    @GET("posts")
    suspend fun getAllPosts(
        @Header("Authorization") token: String
    ): ApiResponse<List<PostResponse>>

    @GET("posts/mine")
    suspend fun getUserPosts(
        @Header("Authorization") token: String
    ): ApiResponse<List<PostResponse>>

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("caption") caption: RequestBody,
        @Part("isPublic") isPublic: RequestBody,
        @Part images: MultipartBody.Part
    ): ApiResponse<PostResponse>

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdatePostRequest
    ): ApiResponse<PostResponse>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<String>

    @POST("posts/{id}/like")
    suspend fun toggleLike(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<String>

    @GET("posts/{postId}/comments")
    suspend fun getPostComments(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int
    ): ApiResponse<List<CommentResponse>>

    @POST("posts/{postId}/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int,
        @Body request: CreateCommentRequest
    ): ApiResponse<CommentResponse>
}