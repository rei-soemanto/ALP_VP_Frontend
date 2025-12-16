package com.example.alp_vp_frontend.data.repository

import com.example.alp_vp_frontend.data.dto.CommentResponse
import com.example.alp_vp_frontend.data.dto.CreateCommentRequest
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.dto.UpdatePostRequest
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.service.PostApiService
import com.example.alp_vp_frontend.data.mapper.ResponseErrorMapper
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class PostRepository(
    private val postApiService: PostApiService,
    private val dataStore: DataStoreManager
) {
    private suspend fun getAuthHeader(): String {
        val token = dataStore.tokenFlow.first() ?: ""
        return "Bearer $token"
    }

    suspend fun getAllPosts(): List<PostResponse> {
        try {
            val request = postApiService.getAllPosts(getAuthHeader()).data

            return request
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun getUserPosts(): List<PostResponse> {
        try {
            val request = postApiService.getUserPosts(getAuthHeader()).data

            return request
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun createPost(
        caption: RequestBody,
        isPublic: RequestBody,
        image: MultipartBody.Part
    ): PostResponse {
        try {
            val response = postApiService.createPost(getAuthHeader(), caption, isPublic, image).data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun updatePost(postId: String, caption: String, isPublic: Boolean): PostResponse {
        try {
            val request = UpdatePostRequest(caption = caption, isPublic = isPublic)
            val response = postApiService.updatePost(getAuthHeader(), postId, request).data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun deletePost(postId: String): String {
        try {
            val response = postApiService.deletePost(getAuthHeader(), postId).data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun toggleLike(postId: String): String {
        try {
            val response = postApiService.toggleLike(getAuthHeader(), postId).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun getComments(postId: Int): List<CommentResponse> {
        try {
            val response = postApiService.getPostComments(getAuthHeader(), postId).data
            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }

    suspend fun createComment(
        postId: Int,
        content: String,
        replyingToId: Int? = null
    ): CommentResponse {
        try {
            val request = CreateCommentRequest(content = content, replyingToId = replyingToId)
            val response = postApiService.createComment(getAuthHeader(), postId, request).data

            return response
        } catch (e: HttpException) {
            throw Exception(ResponseErrorMapper.fromHttpException(e))
        }
    }
}