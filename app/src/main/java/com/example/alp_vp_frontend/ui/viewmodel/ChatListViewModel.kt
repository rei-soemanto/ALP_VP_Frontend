package com.example.alp_vp_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ChatListUIState {
    object Loading : ChatListUIState
    object Idle : ChatListUIState
    data class Error(val message: String) : ChatListUIState
}
class ChatListViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatListUIState>(ChatListUIState.Loading)
    val uiState: StateFlow<ChatListUIState> = _uiState.asStateFlow()

    private val _chatList = MutableStateFlow<List<ChatListItem>>(emptyList())
    val chatList: StateFlow<List<ChatListItem>> = _chatList.asStateFlow()

    fun getChatList() {
        viewModelScope.launch {
            _uiState.value = ChatListUIState.Loading
            try {
                _chatList.value = chatRepository.getChatList()
                _uiState.value = ChatListUIState.Idle
            } catch (e: Exception) {
                _uiState.value = ChatListUIState.Error(e.message ?: "Failed to fetch chat list")
            }
        }
    }
}