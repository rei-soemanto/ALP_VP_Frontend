package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class ChatListItem(
    val chatProfile: ChatProfile,
    val content: String,
    val id: Int,
    val read: Boolean,
    val sentByYou: Boolean
)

data class ChatProfile(
    val avatarUrl: Any,
    val fullName: String,
    val id: Int
)