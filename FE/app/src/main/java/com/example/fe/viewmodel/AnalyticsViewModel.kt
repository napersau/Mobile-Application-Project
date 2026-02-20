package com.example.fe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.DailyStudyStatResponse
import com.example.fe.model.UserGamificationResponse
import com.example.fe.repository.AnalyticsRepository
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {

    private val repository = AnalyticsRepository()

    private val _gamification = MutableLiveData<Result<UserGamificationResponse>>()
    val gamification: LiveData<Result<UserGamificationResponse>> = _gamification

    private val _studyStats = MutableLiveData<Result<List<DailyStudyStatResponse>>>()
    val studyStats: LiveData<Result<List<DailyStudyStatResponse>>> = _studyStats

    private val _isLoadingGamification = MutableLiveData<Boolean>()
    val isLoadingGamification: LiveData<Boolean> = _isLoadingGamification

    private val _isLoadingStats = MutableLiveData<Boolean>()
    val isLoadingStats: LiveData<Boolean> = _isLoadingStats

    fun loadGamification() {
        viewModelScope.launch {
            _isLoadingGamification.postValue(true)
            try {
                val response = repository.getUserGamification()
                if (response.code == 1000 && response.result != null) {
                    _gamification.postValue(Result.success(response.result))
                } else {
                    _gamification.postValue(Result.failure(Exception(response.message)))
                }
            } catch (e: Exception) {
                Log.e("AnalyticsViewModel", "Error fetching gamification", e)
                _gamification.postValue(Result.failure(e))
            } finally {
                _isLoadingGamification.postValue(false)
            }
        }
    }

    fun loadStudyStats(days: Int = 7) {
        viewModelScope.launch {
            _isLoadingStats.postValue(true)
            try {
                val response = repository.getStudyStats(days)
                if (response.code == 1000 && response.result != null) {
                    _studyStats.postValue(Result.success(response.result))
                } else {
                    _studyStats.postValue(Result.failure(Exception(response.message)))
                }
            } catch (e: Exception) {
                Log.e("AnalyticsViewModel", "Error fetching study stats", e)
                _studyStats.postValue(Result.failure(e))
            } finally {
                _isLoadingStats.postValue(false)
            }
        }
    }
}

