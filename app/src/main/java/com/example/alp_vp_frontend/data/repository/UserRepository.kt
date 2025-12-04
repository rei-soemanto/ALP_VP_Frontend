package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.service.ApiService

class UserRepository(private val apiService: ApiService) {
    suspend fun getCurrentUser(token: String): UserResponse {
        return apiService.getCurrentUser("Bearer $token").data
    }
}