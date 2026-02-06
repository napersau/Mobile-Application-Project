package com.example.fe.model

data class FlashcardResponse(
    val id: Long,
    val term: String,
    val definition: String,
    val pronunciation: String? = null,
    val example: String? = null,
    val mediaList: List<MediaResponse>? = null,
    val flashcardsProgress: FlashcardsProgressResponse? = null
)
