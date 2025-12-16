package com.example.alp_vp_frontend.data.mapper

import com.example.alp_vp_frontend.data.kt.ResponseError
import com.google.gson.Gson
import retrofit2.HttpException

object ResponseErrorMapper {
    private val gson = Gson()

    fun fromHttpException(e: HttpException): String {
        val body = e.response()
            ?.errorBody()
            ?.string()
            ?: return "Unknown server error"

        return try {
            gson.fromJson(body, ResponseError::class.java).message
        } catch (ex: Exception) {
            "Invalid error response"
        }
    }
}