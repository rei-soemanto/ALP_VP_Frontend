package com.example.alp_vp_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

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

    private var _chatDeque = ArrayDeque<ChatListItem>()
    private val _chatList = MutableStateFlow<List<ChatListItem>>(emptyList())
    val chatList: StateFlow<List<ChatListItem>> = _chatList.asStateFlow()

    fun getChatList() {
        viewModelScope.launch {
            _uiState.value = ChatListUIState.Loading
            try {
                _chatList.value = chatRepository.getChatList()
                _chatDeque = ArrayDeque(_chatList.value)
                _uiState.value = ChatListUIState.Idle
            } catch (e: Exception) {
                _uiState.value = ChatListUIState.Error(e.message ?: "Failed to fetch chat list")
            }
        }
    }

    // Socket.io
    fun connectSocket() {
        println("ChatListViewModel connect socket")

        viewModelScope.launch {
            chatRepository.connect(0)
        }
    }

    fun disconnectSocket() {
        println("ChatListViewModel disconnect socket")

        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }

    fun listenToIncomingMessages() {
        viewModelScope.launch {
            chatRepository.incomingMessages
                .collect { message ->
                    for (chat in _chatDeque) {
                        if (chat.chatProfile.id == message.senderId) {
                            _chatDeque.remove(chat)
                            _chatDeque.addFirst(chat.copy(
                                content = message.content,
                                read = false
                            ))
                            break
                        }
                    }

//                    println(message)

                    _chatList.value = _chatDeque.toList()
                }
        }
    }

    fun listenToReconnects() {
        viewModelScope.launch {
            chatRepository.onReconnect.collect {
                try {
                    getChatList()
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }

    init {
        listenToReconnects()
        listenToIncomingMessages()
    }

//    override fun onCleared() {
//        disconnectSocket()
//    }
}