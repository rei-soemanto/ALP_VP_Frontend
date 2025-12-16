package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.kt.CommentResponse
import com.example.alp_vp_frontend.data.kt.CreateCommentRequest
import com.example.alp_vp_frontend.data.kt.PostResponse
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

    suspend fun getComments(token: String, postId: Int): List<CommentResponse> {
        return postApiService.getPostComments("Bearer $token", postId).data
    }

    suspend fun createComment(
        token: String,
        postId: Int,
        content: String,
        replyingToId: Int? = null
    ): CommentResponse {

        val request = CreateCommentRequest(
            content = content,
            replyingToId = replyingToId
        )

        return postApiService.createComment("Bearer $token", postId, request).data
    }
}