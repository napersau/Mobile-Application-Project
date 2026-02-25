package com.example.fe.repository

import com.example.fe.model.VNPayResponse
import com.example.fe.network.PaymentApiService
import com.example.fe.network.RetrofitClient

class PaymentRepository {

    private val api: PaymentApiService = RetrofitClient.paymentApi

    suspend fun createVnPayPayment(courseId: Long): Result<VNPayResponse> {
        return try {
            val response = api.createVnPayPayment(courseId = courseId, platform = "android")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result != null) {
                    Result.success(body.result)
                } else {
                    Result.failure(Exception(body?.message ?: "Không tạo được link thanh toán"))
                }
            } else {
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

