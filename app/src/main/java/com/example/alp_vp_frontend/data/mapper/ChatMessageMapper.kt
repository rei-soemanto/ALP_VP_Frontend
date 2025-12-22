package com.example.alp_vp_frontend.data.mapper

import com.example.alp_vp_frontend.data.dto.ChatMessage
import com.google.gson.Gson
import org.json.JSONObject

object ChatMessageMapper {

    private val gson = Gson()

    fun fromSocketArgs(args: Array<Any>): ChatMessage? {
        if (args.isEmpty()) return null

        val json = when (val payload = args[0]) {
            is JSONObject -> payload.toString()
            is String -> payload
            else -> return null
        }

        return try {
            gson.fromJson(json, ChatMessage::class.java)
        } catch (e: Exception) {
            null
        }
    }
}