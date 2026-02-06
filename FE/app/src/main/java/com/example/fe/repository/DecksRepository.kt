package com.example.fe.repository

import com.example.fe.network.RetrofitClient

class DecksRepository {

    suspend fun getAllDecks() = RetrofitClient.decksApi.getAllDecks()

    suspend fun getDeckById(id: Long) = RetrofitClient.decksApi.getDeckById(id)
}
