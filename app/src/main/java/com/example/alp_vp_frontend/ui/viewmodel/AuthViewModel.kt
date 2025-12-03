package com.example.alp_vp_frontend.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.AddInterestRequest
import com.example.alp_vp_frontend.data.dto.InterestResponse
import com.example.alp_vp_frontend.data.dto.UserResponse
import com.example.alp_vp_frontend.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserResponse) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var authState: AuthUiState by mutableStateOf(AuthUiState.Idle)
        private set

    var interestList: List<InterestResponse> by mutableStateOf(emptyList())
    var selectedInterests = mutableStateListOf<Int>()

    fun register(fullName: String, email: String, pass: String) {
        viewModelScope.launch {
            authState = AuthUiState.Loading
            try {
                val user = repository.register(fullName, email, pass)
                authState = AuthUiState.Success(user)

                fetchInterests()
            } catch (e: Exception) {
                authState = AuthUiState.Error(e.message ?: "Registration Failed")
            }
        }
    }

    fun fetchInterests() {
        viewModelScope.launch {
            try {
                interestList = repository.getInterests()
            } catch (e: Exception) {
                println("Error fetching interests: ${e.message}")
            }
        }
    }

    fun submitSelectedInterests(token: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            authState = AuthUiState.Loading
            try {
                val requestList = selectedInterests.map { id ->
                    AddInterestRequest(interestId = id)
                }

                repository.submitInterests(token, requestList)
                onComplete()
            } catch (e: Exception) {
                authState = AuthUiState.Error(e.message ?: "Failed to save interests")
            }
        }
    }

    fun toggleInterestSelection(id: Int) {
        if (selectedInterests.contains(id)) {
            selectedInterests.remove(id)
        } else {
            selectedInterests.add(id)
        }
    }
}