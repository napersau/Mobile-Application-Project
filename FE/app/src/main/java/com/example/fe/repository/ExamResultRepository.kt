package com.example.fe.repository

import com.example.fe.model.ExamResultRequest
import com.example.fe.model.ExamResultResponse
import com.example.fe.network.RetrofitClient

class ExamResultRepository {

    private val api = RetrofitClient.examResultApi

    suspend fun createExamResult(request: ExamResultRequest): Result<ExamResultResponse> {
        return try {
            val response = api.createExamResult(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Failed to create exam result: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamResultById(id: Long): Result<ExamResultResponse> {
        return try {
            val response = api.getExamResultById(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Failed to get exam result: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

