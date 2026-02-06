package com.example.fe.model

data class FlashcardsProgressResponse(
    val id: Long,
    val status: String? = null,
    val lastReviewed: String? = null,
    val nextReview: String? = null,
    val reviewCount: Int? = 0,
    val difficulty: String? = null
)
