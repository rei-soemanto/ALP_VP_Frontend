package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.service.PostApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepository(private val postApiService: PostApiService) {
    suspend fun getMyPosts(token: String): List<PostResponse> {
        return postApiService.getUserPosts("Bearer $token").data
    }

    suspend fun createPost(token: String, caption: RequestBody, isPublic: RequestBody, image: MultipartBody.Part): PostResponse {
        return postApiService.createPost("Bearer $token", caption, isPublic, image).data
    }
}