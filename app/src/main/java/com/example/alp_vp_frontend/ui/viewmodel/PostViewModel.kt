package com.example.alp_vp_frontend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.dto.UpdatePostRequest
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.repository.PostRepository
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class Post(
    val id: String,
    val username: String,
    val date: String,
    val likes: String,
    val comments: String,
    val caption: String,
    val imageUrl: String,
    val avatarUrl: String?,
    val isPublic: Boolean,
    val isLiked: Boolean
)

class PostViewModel(
    private val apiService: PostApiService,
    private val repository: PostRepository,
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
                _posts.value = response.data.map { it.toUiModel() }
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
                _userPosts.value = response.data.map { it.toUiModel() }
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
                    repository.createPost("Bearer $token", captionPart, publicPart, imagePart)
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

                val request = UpdatePostRequest(
                    caption = caption,
                    isPublic = isPublic
                )

                apiService.updatePost("Bearer $token", postId, request)

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

    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            try {
                val token = dataStore.tokenFlow.first() ?: return@launch

                updateLocalLikeState(postId, isCurrentlyLiked)

                apiService.toggleLike("Bearer $token", postId)

            } catch (e: Exception) {
                e.printStackTrace()
                updateLocalLikeState(postId, !isCurrentlyLiked)
            }
        }
    }

    private fun updateLocalLikeState(postId: String, wasLiked: Boolean) {
        fun updateList(list: List<Post>): List<Post> {
            return list.map { post ->
                if (post.id == postId) {
                    val currentLikes = post.likes.toIntOrNull() ?: 0
                    val newLikes = if (wasLiked) currentLikes - 1 else currentLikes + 1
                    post.copy(
                        isLiked = !wasLiked,
                        likes = newLikes.toString()
                    )
                } else {
                    post
                }
            }
        }

        _posts.value = updateList(_posts.value)
        _userPosts.value = updateList(_userPosts.value)
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

            MultipartBody.Part.createFormData("images", file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun PostResponse.toUiModel(): Post {
        val firstImage = this.images?.firstOrNull()?.imageUrl ?: ""

        val BASE_URL = "http://10.0.2.2:3000"

        val fullUrl = if (firstImage.startsWith("http")) firstImage else "$BASE_URL$firstImage"

        return Post(
            id = this.id.toString(),
            username = this.author.fullName,
            date = formatDate(this.createdAt),
            likes = this.totalLikes.toString(),
            comments = this.totalComments.toString(),
            caption = this.caption ?: "",
            imageUrl = fullUrl,
            avatarUrl = this.author.avatarUrl,
            isPublic = this.isPublic,
            isLiked = this.isLiked
        )
    }
}