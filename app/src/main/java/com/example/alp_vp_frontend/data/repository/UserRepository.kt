package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.service.ApiService
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class UserRepository(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) {
    // Helper to get token internally
    private suspend fun getAuthHeader(): String {
        val token = dataStore.tokenFlow.first() ?: ""
        return "Bearer $token"
    }

    suspend fun getCurrentUser(): UserResponse {
        try {
            return apiService.getCurrentUser(getAuthHeader()).data
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun updateUser(
        fullName: String,
        about: String,
        avatar: MultipartBody.Part?
    ): UserResponse {
        try {
            val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
            val aboutPart = about.toRequestBody("text/plain".toMediaTypeOrNull())

            return apiService.updateUser(
                getAuthHeader(),
                namePart,
                aboutPart,
                avatar
            ).data
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }
}