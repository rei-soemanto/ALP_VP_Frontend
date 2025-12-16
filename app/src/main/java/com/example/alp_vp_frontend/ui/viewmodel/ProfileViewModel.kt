package com.example.alp_vp_frontend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.kt.PostResponse
import com.example.alp_vp_frontend.data.kt.UserResponse
import com.example.alp_vp_frontend.data.local.DataStoreManager
import com.example.alp_vp_frontend.data.repository.PostRepository
import com.example.alp_vp_frontend.data.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: UserResponse, val posts: List<PostResponse>) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var profileState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    var selectedTabIndex by mutableStateOf(0)

    init { fetchProfileData() }

    fun fetchProfileData() {
        viewModelScope.launch {
            profileState = ProfileUiState.Loading
            try {
                val token = dataStoreManager.tokenFlow.first()
                if (token.isNullOrEmpty()) {
                    profileState = ProfileUiState.Error("Not logged in")
                    return@launch
                }
                val userDeferred = async { userRepository.getCurrentUser(token) }
                val postsDeferred = async { postRepository.getMyPosts(token) }

                profileState = ProfileUiState.Success(userDeferred.await(), postsDeferred.await())
            } catch (e: Exception) {
                profileState = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            onLogoutComplete()
        }
    }
}