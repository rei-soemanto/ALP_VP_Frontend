package com.example.alp_vp_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.example.alp_vp_frontend.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _chunkIndex = MutableStateFlow<Int>(1)
    val chunkIndex: StateFlow<Int> = _chunkIndex.asStateFlow()

    fun getMessages(counterPartId: Int) {
        viewModelScope.launch {
            try {
                _messages.value = chatRepository.getMessages(counterPartId, chunkIndex.value)
            } catch (e: Exception) {
                // handle error
                println(e)
            }
        }
    }
}