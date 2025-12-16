package com.example.alp_vp_frontend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.CommentResponse
import com.example.alp_vp_frontend.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CommentUiState {
    object Loading : CommentUiState
    data class Success(val comments: List<CommentResponse>) : CommentUiState
    data class Error(val message: String) : CommentUiState
}

class CommentViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommentUiState>(CommentUiState.Loading)
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    var replyingToComment: CommentResponse? by mutableStateOf(null)
        private set

    fun fetchComments(postId: Int) {
        viewModelScope.launch {
            _uiState.value = CommentUiState.Loading
            try {
                val comments = postRepository.getComments(postId)
                _uiState.value = CommentUiState.Success(comments)
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Failed to load comments")
            }
        }
    }

    fun postComment(postId: Int, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val parentId = replyingToComment?.id
                postRepository.createComment(postId, content, parentId)
                fetchComments(postId)
                replyingToComment = null
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Failed to post comment")
            }
        }
    }

    fun setReplyingTo(comment: CommentResponse?) {
        replyingToComment = comment
    }
}