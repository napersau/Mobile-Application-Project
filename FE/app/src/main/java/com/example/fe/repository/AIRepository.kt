package com.example.fe.repository

import android.content.Context
import com.example.fe.network.RetrofitClient

class AIRepository(private val context: Context) {

    private val api = RetrofitClient.aiApi

    suspend fun chat(message: String): Result<String> {
        return try {
            // Check if user has token before making request
            val token = com.example.fe.utils.TokenManager.getToken(context)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Vui lòng đăng nhập để sử dụng AI Chat"))
            }

            val request = mapOf("message" to message)
            val response = api.chat(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để sử dụng AI Chat."
                    408 -> "Request timeout - AI đang xử lý quá lâu"
                    else -> "Failed to chat: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Yêu cầu mất quá nhiều thời gian (timeout). Vui lòng thử câu hỏi ngắn gọn hơn hoặc thử lại sau."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translate(text: String): Result<String> {
        return try {
            // Check if user has token before making request
            val token = com.example.fe.utils.TokenManager.getToken(context)
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Vui lòng đăng nhập để sử dụng AI Translate"))
            }

            val request = mapOf("text" to text)
            val response = api.translate(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Unknown error"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại để sử dụng AI Translate."
                    408 -> "Request timeout - Dịch quá lâu"
                    else -> "Failed to translate: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Yêu cầu mất quá nhiều thời gian (timeout). Vui lòng thử lại sau."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

