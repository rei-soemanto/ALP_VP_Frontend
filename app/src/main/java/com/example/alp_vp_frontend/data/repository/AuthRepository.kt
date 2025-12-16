package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.AddInterestRequest
import com.example.alp_vp_frontend.data.dto.InterestResponse
import com.example.alp_vp_frontend.data.dto.LoginRequest
import com.example.alp_vp_frontend.data.dto.RegisterRequest
import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import com.example.alp_vp_frontend.data.service.ApiService
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) {
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
        try {
            val request = LoginRequest(email, pass)
            val user = apiService.login(request).data
            dataStoreManager.saveToken(user.token as String)

            return user
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun getInterests(): List<InterestResponse> {
        try {
            val response = apiService.getInterests().data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }

    }

    suspend fun submitInterests(token: String, interests: List<AddInterestRequest>): String {
        try {
            val formattedToken = "Bearer $token"
            val response = apiService.addUserInterests(formattedToken, interests).data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun logout() {
        dataStoreManager.clearToken()
    }
}