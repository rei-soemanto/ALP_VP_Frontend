package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.AddInterestRequest
import com.example.alp_vp_frontend.data.dto.InterestResponse
import com.example.alp_vp_frontend.data.dto.LoginRequest
import com.example.alp_vp_frontend.data.dto.RegisterRequest
import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import com.example.alp_vp_frontend.data.service.ApiService
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