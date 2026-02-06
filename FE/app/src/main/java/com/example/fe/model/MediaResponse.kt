package com.example.fe.model

data class MediaResponse(
    val id: Long,
    val url: String,
    val type: String? = null,
    val description: String? = null
)
