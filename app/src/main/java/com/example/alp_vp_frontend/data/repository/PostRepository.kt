package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.service.ApiService

class PostRepository(private val apiService: ApiService) {
    suspend fun getMyPosts(token: String): List<PostResponse> {
        return apiService.getMyPosts("Bearer $token").data
    }
}