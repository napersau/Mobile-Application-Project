package com.example.fe.model

data class UserRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String
)
