package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("user_avatar")
    val userAvatar: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("likes")
    val likes: Int?,

    @SerializedName("comments")
    val comments: Int?,

    @SerializedName("is_public")
    val isPublic: Boolean?,

    @SerializedName("images")
    val images: List<PostImageResponse>?
)

data class PostImageResponse(
    @SerializedName("image_url")
    val imageUrl: String
)