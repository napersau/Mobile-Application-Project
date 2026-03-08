package com.example.fe.model

data class DecksResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val isPublic: Boolean? = false,
    val totalFlashcards: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val flashcardsList: List<FlashcardResponse>? = null
) {
    val effectiveTotalFlashcards: Int
        get() = totalFlashcards ?: (flashcardsList?.size ?: 0)
}
