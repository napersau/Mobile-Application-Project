package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.DecksResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DecksApi {

    @GET("api/v1/decks/all")
    suspend fun getAllDecks(): ApiResponse<List<DecksResponse>>

    @GET("api/v1/decks/{id}")
    suspend fun getDeckById(@Path("id") id: Long): ApiResponse<DecksResponse>
}
