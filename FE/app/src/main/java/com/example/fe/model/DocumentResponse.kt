package com.example.fe.model

import java.time.Instant

data class DocumentResponse(
    val id: Long,
    val title: String,
    val slug: String,
    val description: String,
    val thumbnail: String,
    val content: String,
    val category: DocCategory,
    val isPublished: Boolean,
    val viewCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)