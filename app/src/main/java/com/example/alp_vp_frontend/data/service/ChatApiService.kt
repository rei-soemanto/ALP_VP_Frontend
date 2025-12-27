package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.ApiResponse
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.example.alp_vp_frontend.data.dto.ListMessageRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @GET("chats/list")
    suspend fun getChatList(
        @Header("Authorization") token: String
    ): ApiResponse<List<ChatListItem>>

    @GET("chats/{counterPartId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("counterPartId") counterPartId: Int,
    ): ApiResponse<List<ChatMessage>>

    @Multipart
    @POST("chats/{counterPartId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("counterPartId") counterPartId: Int,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>
    )

    @PUT("chats/readMessage/{messageId}")
    suspend fun readMessage(
        @Header("Authorization") token: String,
        @Path("messageId") messageId: Int
    )

    @GET("chats/getImages/{messageId}")
    suspend fun getImages(
        @Header("Authorization") token: String,
        @Path("messageId") messageId: Int
    ): ApiResponse<List<String>>
}