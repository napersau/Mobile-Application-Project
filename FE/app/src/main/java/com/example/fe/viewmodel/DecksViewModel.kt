package com.example.fe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.DecksResponse
import com.example.fe.repository.DecksRepository
import kotlinx.coroutines.launch

class DecksViewModel : ViewModel() {

    private val repository = DecksRepository()

    private val _decksListResult = MutableLiveData<Result<List<DecksResponse>>>()
    val decksListResult: LiveData<Result<List<DecksResponse>>> = _decksListResult

    private val _deckDetailResult = MutableLiveData<Result<DecksResponse>>()
    val deckDetailResult: LiveData<Result<DecksResponse>> = _deckDetailResult

    fun getAllDecks() {
        viewModelScope.launch {
            try {
                Log.d("DecksViewModel", "Fetching all decks")
                val response = repository.getAllDecks()
                
                if (response.code == 1000) {
                    Log.d("DecksViewModel", "Fetched ${response.result.size} decks")
                    _decksListResult.postValue(Result.success(response.result))
                } else {
                    Log.e("DecksViewModel", "Failed with code: ${response.code}")
                    _decksListResult.postValue(Result.failure(Exception("Failed to fetch decks")))
                }
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Exception fetching decks", e)
                _decksListResult.postValue(Result.failure(e))
            }
        }
    }

    fun getDeckById(id: Long) {
        viewModelScope.launch {
            try {
                Log.d("DecksViewModel", "Fetching deck with id: $id")
                val response = repository.getDeckById(id)
                
                if (response.code == 1000) {
                    Log.d("DecksViewModel", "Fetched deck: ${response.result.title}")
                    _deckDetailResult.postValue(Result.success(response.result))
                } else {
                    Log.e("DecksViewModel", "Failed with code: ${response.code}")
                    _deckDetailResult.postValue(Result.failure(Exception("Failed to fetch deck detail")))
                }
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Exception fetching deck detail", e)
                _deckDetailResult.postValue(Result.failure(e))
            }
        }
    }
}
