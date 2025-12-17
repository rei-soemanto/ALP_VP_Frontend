package com.example.alp_vp_frontend.data.service

import com.example.alp_vp_frontend.data.dto.ApiResponse
import com.example.alp_vp_frontend.data.dto.ChatListItem
import retrofit2.http.GET
import retrofit2.http.Header

interface ChatApiService {
    @GET("chats/list")
    suspend fun getChatList(
        @Header("Authorization") token: String
    ): ApiResponse<List<ChatListItem>>
}