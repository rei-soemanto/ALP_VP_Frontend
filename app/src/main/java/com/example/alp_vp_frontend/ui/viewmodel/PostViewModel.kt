package com.example.alp_vp_frontend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.service.PostApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class Post(
    val id: String,
    val username: String,
    val date: String,
    val likes: String,
    val comments: String,
    val caption: String,
    val imageUrl: String,
    val avatarUrl: String?
)

class PostViewModel(
    private val apiService: PostApiService,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    init {
        fetchPosts()
        fetchUserPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first()
                if (token.isNullOrEmpty()) return@launch

                val response = apiService.getAllPosts("Bearer $token")
                _posts.value = response.map { it.toUiModel() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserPosts() {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first()
                if (token.isNullOrEmpty()) return@launch

                val response = apiService.getUserPosts("Bearer $token")
                _userPosts.value = response.map { it.toUiModel() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createPost(context: Context, caption: String, imageUri: Uri?, isPublic: Boolean) {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first()
                if (token.isNullOrEmpty()) return@launch

                val captionPart = caption.toRequestBody("text/plain".toMediaTypeOrNull())
                val publicPart = isPublic.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = prepareImagePart(context, imageUri)

                if (imagePart != null) {
                    apiService.createPost("Bearer $token", captionPart, publicPart, imagePart)
                    fetchPosts()
                    fetchUserPosts()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePost(postId: String, caption: String, isPublic: Boolean) {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first()
                if (token.isNullOrEmpty()) return@launch

                val captionPart = caption.toRequestBody("text/plain".toMediaTypeOrNull())
                val publicPart = isPublic.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                apiService.updatePost("Bearer $token", postId, captionPart, publicPart)
                fetchPosts()
                fetchUserPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first()
                if (token.isNullOrEmpty()) return@launch

                apiService.deletePost("Bearer $token", postId)

                _posts.value = _posts.value.filter { it.id != postId }
                _userPosts.value = _userPosts.value.filter { it.id != postId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun prepareImagePart(context: Context, uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return try {
            val file = File(context.cacheDir, "temp_upload_image.jpg")
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun PostResponse.toUiModel(): Post {
        val firstImage = this.images?.firstOrNull()?.imageUrl ?: ""
        val BASE_URL = "http://10.0.2.2:3000/api/"

        val fullUrl = if (firstImage.startsWith("http")) firstImage else "$BASE_URL$firstImage"

        return Post(
            id = this.id.toString(),
            username = this.username ?: "Unknown",
            date = this.createdAt ?: "Recently",
            likes = (this.likes ?: 0).toString(),
            comments = (this.comments ?: 0).toString(),
            caption = this.caption ?: "",
            imageUrl = fullUrl,
            avatarUrl = this.userAvatar
        )
    }
}