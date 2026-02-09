package com.example.fe.repository

import com.example.fe.model.FlashcardsRequest
import com.example.fe.network.RetrofitClient

class FlashcardsRepository {

    suspend fun createFlashcard(flashcardRequest: FlashcardsRequest) = 
        RetrofitClient.flashcardsApi.createFlashcard(flashcardRequest)

    suspend fun getAllFlashcards() = 
        RetrofitClient.flashcardsApi.getAllFlashcards()

    suspend fun getFlashcardById(id: Long) = 
        RetrofitClient.flashcardsApi.getFlashcardById(id)
}