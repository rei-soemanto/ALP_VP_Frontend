package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("caption") val caption: String?,
    @SerializedName("isPublic") val isPublic: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("author") val author: AuthorResponse,
    @SerializedName("images") val images: List<PostImageResponse>?,
    @SerializedName("totalLikes") val totalLikes: Int,
    @SerializedName("totalComments") val totalComments: Int,
    @SerializedName("isLiked") val isLiked: Boolean
)

data class AuthorResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class PostImageResponse(
    @SerializedName("imageUrl") val imageUrl: String
)

data class UpdatePostRequest(
    @SerializedName("caption") val caption: String,
    @SerializedName("isPublic") val isPublic: Boolean
)