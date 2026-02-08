package com.example.fe.model

data class AuthenticationResponse(
    val token: String,
    val authenticated: Boolean
)