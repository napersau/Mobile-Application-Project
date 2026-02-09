package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.FlashcardResponse
import com.example.fe.model.FlashcardsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FlashcardsApi {

    @POST("api/v1/flashcards")
    suspend fun createFlashcard(@Body flashcardRequest: FlashcardsRequest): ApiResponse<FlashcardResponse>

    @GET("api/v1/flashcards/all")
    suspend fun getAllFlashcards(): ApiResponse<List<FlashcardResponse>>

    @GET("api/v1/flashcards/{id}")
    suspend fun getFlashcardById(@Path("id") id: Long): ApiResponse<FlashcardResponse>
}