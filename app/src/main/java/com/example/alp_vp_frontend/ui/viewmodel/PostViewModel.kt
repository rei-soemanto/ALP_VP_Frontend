package com.example.alp_vp_frontend.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.PostResponse
import com.example.alp_vp_frontend.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

sealed class PostUiState {
    object Idle : PostUiState()
    object Loading : PostUiState()
    data class Success(val message: String? = null) : PostUiState()
    data class Error(val message: String) : PostUiState()
}

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
    private val repository: PostRepository
) : ViewModel() {

    private val _postUiState = MutableStateFlow<PostUiState>(PostUiState.Idle)
    val postUiState: StateFlow<PostUiState> = _postUiState.asStateFlow()

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
            _postUiState.value = PostUiState.Loading
            try {
                val result = repository.getAllPosts()
                _posts.value = result.map { it.toUiModel() }
                _postUiState.value = PostUiState.Success()
            } catch (e: Exception) {
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to fetch posts")
            }
        }
    }

    fun fetchUserPosts() {
        viewModelScope.launch {
            try {
                val result = repository.getUserPosts()
                _userPosts.value = result.map { it.toUiModel() }
            } catch (e: Exception) {
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to fetch user posts")
            }
        }
    }

    fun createPost(context: Context, caption: String, imageUri: Uri?, isPublic: Boolean) {
        viewModelScope.launch {
            _postUiState.value = PostUiState.Loading
            try {
                val captionPart = caption.toRequestBody("text/plain".toMediaTypeOrNull())
                val publicPart = isPublic.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = prepareImagePart(context, imageUri)

                if (imagePart != null) {
                    repository.createPost(captionPart, publicPart, imagePart)
                    _postUiState.value = PostUiState.Success("Post created successfully")

                    fetchPosts()
                    fetchUserPosts()
                } else {
                    _postUiState.value = PostUiState.Error("Image is required")
                }
            } catch (e: Exception) {
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to create post")
            }
        }
    }

    fun updatePost(postId: String, caption: String, isPublic: Boolean) {
        viewModelScope.launch {
            _postUiState.value = PostUiState.Loading
            try {
                repository.updatePost(postId, caption, isPublic)
                _postUiState.value = PostUiState.Success("Post updated")

                fetchPosts()
                fetchUserPosts()
            } catch (e: Exception) {
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to update post")
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            _postUiState.value = PostUiState.Loading
            try {
                repository.deletePost(postId)

                _posts.value = _posts.value.filter { it.id != postId }
                _userPosts.value = _userPosts.value.filter { it.id != postId }

                _postUiState.value = PostUiState.Success("Post deleted")
            } catch (e: Exception) {
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to delete post")
            }
        }
    }

    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            try {
                updateLocalLikeState(postId, isCurrentlyLiked)
                repository.toggleLike(postId)
            } catch (e: Exception) {
                updateLocalLikeState(postId, !isCurrentlyLiked)
                _postUiState.value = PostUiState.Error(e.message ?: "Failed to like post")
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