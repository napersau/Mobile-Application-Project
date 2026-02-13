package com.example.fe.repository

import com.example.fe.network.RetrofitClient

class AIRepository {

    private val api = RetrofitClient.aiApi

    suspend fun chat(message: String): Result<String> {
        return try {
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
                Result.failure(Exception("Failed to chat: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translate(text: String): Result<String> {
        return try {
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
                Result.failure(Exception("Failed to translate: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

