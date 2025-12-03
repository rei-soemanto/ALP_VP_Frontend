package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data") val data: T
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AddInterestRequest(
    @SerializedName("interestId") val interestId: Int,
    @SerializedName("isPrimary") val isPrimary: Boolean = false
)

data class UserResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("about") val about: String?
)

data class InterestResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)