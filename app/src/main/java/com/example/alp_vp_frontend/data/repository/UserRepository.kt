package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.service.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository(private val apiService: ApiService) {
    suspend fun getCurrentUser(token: String): UserResponse {
        return apiService.getCurrentUser("Bearer $token").data
    }

    suspend fun updateUser(
        token: String,
        fullName: String,
        about: String,
        avatar: MultipartBody.Part?
    ): UserResponse {

        val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val aboutPart = about.toRequestBody("text/plain".toMediaTypeOrNull())

        return apiService.updateUser(
            "Bearer $token",
            namePart,
            aboutPart,
            avatar
        ).data
    }
}