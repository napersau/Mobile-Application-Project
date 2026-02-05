package com.example.fe.model

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String
)