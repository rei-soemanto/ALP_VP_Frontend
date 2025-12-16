package com.example.alp_vp_frontend.data.dto

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)