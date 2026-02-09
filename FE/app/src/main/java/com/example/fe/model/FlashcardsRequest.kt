package com.example.fe.model

data class FlashcardsRequest(
    val id: Long? = null,
    val term: String,
    val definition: String,
    val pronunciation: String? = null,
    val example: String? = null,
    val mediaList: List<MediaRequest>? = null
)