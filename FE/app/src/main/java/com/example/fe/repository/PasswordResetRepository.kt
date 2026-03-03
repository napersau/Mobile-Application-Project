package com.example.fe.repository

import com.example.fe.model.ForgotPasswordRequest
import com.example.fe.network.RetrofitClient

class PasswordResetRepository {

    suspend fun sendOtp(email: String) =
        RetrofitClient.emailApi.forgotPassword(ForgotPasswordRequest(email = email))

    suspend fun verifyOtp(email: String, otp: String) =
        RetrofitClient.emailApi.verifyOtp(ForgotPasswordRequest(email = email, otp = otp))

    suspend fun resetPassword(email: String, otp: String, newPassword: String) =
        RetrofitClient.emailApi.resetPassword(
            ForgotPasswordRequest(email = email, otp = otp, newPassword = newPassword)
        )
}

