package com.example.alp_vp_frontend.data.repository;

import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.local.DataStoreManager;
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import com.example.alp_vp_frontend.data.service.ChatApiService;
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class ChatRepository(
    private val chatApiService: ChatApiService,
    private val datastore: DataStoreManager
) {
    private suspend fun getAuthHeader(): String {
        val token = datastore.tokenFlow.first() ?: ""
        return "Bearer $token"
    }

    suspend fun getChatList(): List<ChatListItem> {
        try {
            val response = chatApiService.getChatList(getAuthHeader()).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }
}
