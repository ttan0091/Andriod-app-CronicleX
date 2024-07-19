package com.example.chronicle.utils

import com.example.chronicle.response.ChatRequest
import com.example.chronicle.response.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @POST("chat/completions")
    fun createChatCompletion(
        @Body chatRequest: ChatRequest,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String = "Bearer $OPENAI_API_KEY"
    ): Call<ChatResponse>
}