package com.example.fe.model

data class ForgotPasswordRequest(
    val email: String? = null,
    val otp: String? = null,
    val newPassword: String? = null
)

