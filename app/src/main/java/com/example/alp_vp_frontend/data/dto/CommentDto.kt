package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class CreateCommentRequest(
    @SerializedName("content") val content: String,
    @SerializedName("replyingToId") val replyingToId: Int? = null
)

data class CommentResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("author") val author: AuthorResponse,
    @SerializedName("replyingToId") val replyingToId: Int?,
    @SerializedName("totalReplies") val totalReplies: Int,
    @SerializedName("replies") val replies: List<CommentResponse>? = emptyList()
)