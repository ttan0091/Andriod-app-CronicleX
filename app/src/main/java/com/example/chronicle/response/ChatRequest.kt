package com.example.chronicle.response

data class ChatRequest(
    val messages: List<Message>,
    val model: String
)
