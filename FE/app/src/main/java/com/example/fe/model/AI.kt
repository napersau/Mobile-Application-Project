package com.example.fe.model

// AI Chat models
data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val response: String
)

// Translate models
data class TranslateRequest(
    val text: String
)

data class TranslateResponse(
    val translation: String
)

// Chat message for UI
data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isUser: Boolean, // true = user, false = AI
    val timestamp: Long = System.currentTimeMillis()
)

