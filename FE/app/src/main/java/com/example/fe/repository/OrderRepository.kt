package com.example.fe.repository

import com.example.fe.model.OrderResponse
import com.example.fe.network.OrderApiService
import com.example.fe.network.RetrofitClient

class OrderRepository {

    private val api: OrderApiService = RetrofitClient.orderApi

    suspend fun processSuccessfulPayment(vnpTxnRef: String): Result<Unit> {
        return try {
            val response = api.processSuccessfulPayment(vnpTxnRef)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Xử lý thanh toán thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllOrders(): Result<List<OrderResponse>> {
        return try {
            val response = api.getAllOrders()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000) {
                    Result.success(body.result ?: emptyList())
                } else {
                    Result.failure(Exception(body?.message ?: "Không tải được danh sách đơn hàng"))
                }
            } else {
                Result.failure(Exception("Lỗi kết nối: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

