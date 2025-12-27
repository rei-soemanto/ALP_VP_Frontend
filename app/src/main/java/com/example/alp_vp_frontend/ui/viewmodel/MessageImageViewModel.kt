package com.example.alp_vp_frontend.ui.viewmodel

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alp_vp_frontend.data.dto.ChatListItem
import com.example.alp_vp_frontend.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageImageViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _imageURLs = MutableStateFlow<List<String>>(emptyList())
    val imageURLs: StateFlow<List<String>> = _imageURLs.asStateFlow()

    fun getImages(messageId: Int) {
        viewModelScope.launch {
            try {
                _imageURLs.value = chatRepository.getImages(messageId)
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}