package com.example.fe.model

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String
)