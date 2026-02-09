package com.example.fe.model

data class DecksRequest(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val isPublic: Boolean? = false
)