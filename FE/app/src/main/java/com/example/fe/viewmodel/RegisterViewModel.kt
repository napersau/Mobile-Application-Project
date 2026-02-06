package com.example.fe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.UserRequest
import com.example.fe.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun register(request: UserRequest) {
        viewModelScope.launch {
            try {
                val response = repository.register(request)
                if (response.code == 1000) {
                    _registerResult.value =
                        Result.success(response.message ?: "Đăng ký thành công")
                } else {
                    _registerResult.value =
                        Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }
}