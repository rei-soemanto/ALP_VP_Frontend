package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.kt.AddInterestRequest
import com.example.alp_vp_frontend.data.kt.InterestResponse
import com.example.alp_vp_frontend.data.kt.LoginRequest
import com.example.alp_vp_frontend.data.kt.RegisterRequest
import com.example.alp_vp_frontend.data.kt.UserResponse
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import com.example.alp_vp_frontend.data.service.ApiService
import com.example.alp_vp_frontend.ui.viewmodel.AuthUiState
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService) {
    suspend fun register(fullName: String, email: String, pass: String): UserResponse {
        try {
            val request = RegisterRequest(fullName, email, pass)
            val response = apiService.register(request)

            return response.data
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun login(email: String, pass: String): UserResponse {
        val request = LoginRequest(email, pass)
        return apiService.login(request).data
    }

    suspend fun getInterests(): List<InterestResponse> {
        return apiService.getInterests().data
    }

    suspend fun submitInterests(token: String, interests: List<AddInterestRequest>): String {
        val formattedToken = "Bearer $token"
        return apiService.addUserInterests(formattedToken, interests).data
    }
}