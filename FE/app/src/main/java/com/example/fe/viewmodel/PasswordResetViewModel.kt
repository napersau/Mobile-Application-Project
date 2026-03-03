package com.example.fe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.repository.PasswordResetRepository
import kotlinx.coroutines.launch

class PasswordResetViewModel : ViewModel() {

    private val repository = PasswordResetRepository()

    private val _sendOtpResult = MutableLiveData<Result<String>>()
    val sendOtpResult: LiveData<Result<String>> = _sendOtpResult

    private val _verifyOtpResult = MutableLiveData<Result<String>>()
    val verifyOtpResult: LiveData<Result<String>> = _verifyOtpResult

    private val _resetPasswordResult = MutableLiveData<Result<String>>()
    val resetPasswordResult: LiveData<Result<String>> = _resetPasswordResult

    fun sendOtp(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.sendOtp(email)
                if (response.code == 1000) {
                    _sendOtpResult.postValue(Result.success(response.message))
                } else {
                    _sendOtpResult.postValue(Result.failure(Exception(response.message)))
                }
            } catch (e: Exception) {
                _sendOtpResult.postValue(Result.failure(Exception("Lỗi kết nối: ${e.message}")))
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(email, otp)
                if (response.code == 1000) {
                    _verifyOtpResult.postValue(Result.success(response.message))
                } else {
                    _verifyOtpResult.postValue(Result.failure(Exception(response.message)))
                }
            } catch (e: Exception) {
                _verifyOtpResult.postValue(Result.failure(Exception("Lỗi kết nối: ${e.message}")))
            }
        }
    }

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetPassword(email, otp, newPassword)
                if (response.code == 1000) {
                    _resetPasswordResult.postValue(Result.success(response.message))
                } else {
                    _resetPasswordResult.postValue(Result.failure(Exception(response.message)))
                }
            } catch (e: Exception) {
                _resetPasswordResult.postValue(Result.failure(Exception("Lỗi kết nối: ${e.message}")))
            }
        }
    }
}

