package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.ApiResponse
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ChatApiService {
    @GET("chats/list")
    suspend fun getChatList(
        @Header("Authorization") token: String
    ): ApiResponse<List<ChatListItem>>

    @GET("chats/chat/{counterPartId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("counterPartId") counterPartId: Int
    ): ApiResponse<List<ChatMessage>>
}