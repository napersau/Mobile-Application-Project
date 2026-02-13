package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.ExamResultRequest
import com.example.fe.model.ExamResultResponse
import retrofit2.Response
import retrofit2.http.*

interface ExamResultApi {

    @POST("api/v1/exam-results")
    suspend fun createExamResult(
        @Body request: ExamResultRequest
    ): Response<ApiResponse<ExamResultResponse>>

    @GET("api/v1/exam-results/{id}")
    suspend fun getExamResultById(
        @Path("id") id: Long
    ): Response<ApiResponse<ExamResultResponse>>
}

