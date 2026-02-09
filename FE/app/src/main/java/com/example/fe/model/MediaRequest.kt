package com.example.fe.model

data class MediaRequest(
    val id: Long? = null,
    val url: String,
    val type: String? = null,
    val description: String? = null
)