package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.ExamRequest
import com.example.fe.model.ExamResponse
import com.example.fe.model.ExamType
import com.example.fe.repository.ExamRepository
import kotlinx.coroutines.launch

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExamRepository()

    private val _examsLiveData = MutableLiveData<Result<List<ExamResponse>>>()
    val examsLiveData: LiveData<Result<List<ExamResponse>>> = _examsLiveData

    private val _examDetailLiveData = MutableLiveData<Result<ExamResponse>>()
    val examDetailLiveData: LiveData<Result<ExamResponse>> = _examDetailLiveData

    private val _createExamLiveData = MutableLiveData<Result<ExamResponse>>()
    val createExamLiveData: LiveData<Result<ExamResponse>> = _createExamLiveData

    private val _deleteExamLiveData = MutableLiveData<Result<Unit>>()
    val deleteExamLiveData: LiveData<Result<Unit>> = _deleteExamLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getAllExams() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllExams()
                _examsLiveData.postValue(result)
                if (result.isFailure) {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _examsLiveData.postValue(Result.failure(e))
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getExamsByType(examType: ExamType) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getExamsByType(examType.name)
                _examsLiveData.postValue(result)
                if (result.isFailure) {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _examsLiveData.postValue(Result.failure(e))
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getExamById(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getExamById(id)
                _examDetailLiveData.postValue(result)
                if (result.isFailure) {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _examDetailLiveData.postValue(Result.failure(e))
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createExam(request: ExamRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.createExam(request)
                _createExamLiveData.postValue(result)
                if (result.isFailure) {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _createExamLiveData.postValue(Result.failure(e))
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateExam(id: Long, request: ExamRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.updateExam(id, request)
                if (result.isSuccess) {
                    getExamById(id)
                } else {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteExam(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteExam(id)
                _deleteExamLiveData.postValue(result)
                if (result.isSuccess) {
                    getAllExams() // Refresh list
                } else {
                    _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _deleteExamLiveData.postValue(Result.failure(e))
                _errorMessage.postValue(e.message ?: "Unknown error")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

