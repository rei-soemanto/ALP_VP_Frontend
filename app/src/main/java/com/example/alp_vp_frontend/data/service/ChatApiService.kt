package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.ApiResponse
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.example.alp_vp_frontend.data.dto.ListMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @GET("chats/{counterPartId/images")
    suspend fun getImages(
        @Header("Authorization") token: String,
        @Path("counterPartId") counterPartId: Int,
        @Query("messageId") chunkIndex: Int
    ): ApiResponse<List<String>>
}