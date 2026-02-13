package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.ExamRequest
import com.example.fe.model.ExamResponse
import retrofit2.Response
import retrofit2.http.*

interface ExamApi {

    @GET("api/v1/exams")
    suspend fun getAllExams(): Response<ApiResponse<List<ExamResponse>>>

    @GET("api/v1/exams/type")
    suspend fun getExamsByType(
        @Query("examType") examType: String
    ): Response<ApiResponse<List<ExamResponse>>>

    @GET("api/v1/exams/{id}")
    suspend fun getExamById(
        @Path("id") id: Long
    ): Response<ApiResponse<ExamResponse>>

    @POST("api/v1/exams")
    suspend fun createExam(
        @Body request: ExamRequest
    ): Response<ApiResponse<ExamResponse>>

    @PUT("api/v1/exams/{id}")
    suspend fun updateExam(
        @Path("id") id: Long,
        @Body request: ExamRequest
    ): Response<ApiResponse<ExamResponse>>

    @DELETE("api/v1/exams/{id}")
    suspend fun deleteExam(
        @Path("id") id: Long
    ): Response<ApiResponse<Void>>
}

