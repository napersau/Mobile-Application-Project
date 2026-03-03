package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.ForgotPasswordRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface EmailApi {

    @POST("api/v1/email/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<String>

    @POST("api/v1/email/verify-otp")
    suspend fun verifyOtp(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<String>

    @POST("api/v1/email/reset-password")
    suspend fun resetPassword(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<String>
}

