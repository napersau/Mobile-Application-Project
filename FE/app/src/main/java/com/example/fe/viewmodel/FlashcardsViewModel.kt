package com.example.fe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.FlashcardResponse
import com.example.fe.model.FlashcardsRequest
import com.example.fe.repository.FlashcardsRepository
import kotlinx.coroutines.launch

class FlashcardsViewModel : ViewModel() {

    private val repository = FlashcardsRepository()

    private val _flashcardsListResult = MutableLiveData<Result<List<FlashcardResponse>>>()
    val flashcardsListResult: LiveData<Result<List<FlashcardResponse>>> = _flashcardsListResult

    private val _flashcardDetailResult = MutableLiveData<Result<FlashcardResponse>>()
    val flashcardDetailResult: LiveData<Result<FlashcardResponse>> = _flashcardDetailResult

    private val _createFlashcardResult = MutableLiveData<Result<FlashcardResponse>>()
    val createFlashcardResult: LiveData<Result<FlashcardResponse>> = _createFlashcardResult

    fun getAllFlashcards() {
        viewModelScope.launch {
            try {
                Log.d("FlashcardsViewModel", "Fetching all flashcards")
                val response = repository.getAllFlashcards()
                
                if (response.code == 1000) {
                    Log.d("FlashcardsViewModel", "Fetched ${response.result.size} flashcards")
                    _flashcardsListResult.postValue(Result.success(response.result))
                } else {
                    Log.e("FlashcardsViewModel", "Failed with code: ${response.code}")
                    _flashcardsListResult.postValue(Result.failure(Exception("Failed to fetch flashcards")))
                }
            } catch (e: Exception) {
                Log.e("FlashcardsViewModel", "Exception fetching flashcards", e)
                _flashcardsListResult.postValue(Result.failure(e))
            }
        }
    }

    fun getFlashcardById(id: Long) {
        viewModelScope.launch {
            try {
                Log.d("FlashcardsViewModel", "Fetching flashcard with id: $id")
                val response = repository.getFlashcardById(id)
                
                if (response.code == 1000) {
                    Log.d("FlashcardsViewModel", "Fetched flashcard: ${response.result.term}")
                    _flashcardDetailResult.postValue(Result.success(response.result))
                } else {
                    Log.e("FlashcardsViewModel", "Failed with code: ${response.code}")
                    _flashcardDetailResult.postValue(Result.failure(Exception("Failed to fetch flashcard detail")))
                }
            } catch (e: Exception) {
                Log.e("FlashcardsViewModel", "Exception fetching flashcard detail", e)
                _flashcardDetailResult.postValue(Result.failure(e))
            }
        }
    }

    fun createFlashcard(flashcardRequest: FlashcardsRequest) {
        viewModelScope.launch {
            try {
                Log.d("FlashcardsViewModel", "Creating flashcard: ${flashcardRequest.term}")
                val response = repository.createFlashcard(flashcardRequest)
                
                if (response.code == 1000) {
                    Log.d("FlashcardsViewModel", "Created flashcard successfully")
                    _createFlashcardResult.postValue(Result.success(response.result))
                } else {
                    Log.e("FlashcardsViewModel", "Failed with code: ${response.code}")
                    _createFlashcardResult.postValue(Result.failure(Exception("Failed to create flashcard")))
                }
            } catch (e: Exception) {
                Log.e("FlashcardsViewModel", "Exception creating flashcard", e)
                _createFlashcardResult.postValue(Result.failure(e))
            }
        }
    }
}