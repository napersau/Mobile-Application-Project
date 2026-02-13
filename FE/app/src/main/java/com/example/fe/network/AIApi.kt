package com.example.fe.network

import com.example.fe.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface AIApi {

    @POST("api/v1/ai/chat")
    suspend fun chat(
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>

    @POST("api/v1/ai/translate")
    suspend fun translate(
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>
}

