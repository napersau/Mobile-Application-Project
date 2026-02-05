package com.example.fe.model

data class ApiResponse<T>(
    val code: Int,
    val result: T,
    var message: String
)