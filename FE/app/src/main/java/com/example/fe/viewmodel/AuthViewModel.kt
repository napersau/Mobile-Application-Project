package com.example.fe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.AuthenticationResponse
import com.example.fe.repository.AuthRepository
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<AuthenticationResponse>>()
    val loginResult: LiveData<Result<AuthenticationResponse>> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting login for: $username")
                val response = repository.login(username, password)
                Log.d("AuthViewModel", "Response code: ${response.code}")
                
                if (response.code == 1000) {
                    Log.d("AuthViewModel", "Login successful, posting success")
                    _loginResult.postValue(Result.success(response.result))
                } else {
                    Log.e("AuthViewModel", "Login failed with code: ${response.code}")
                    _loginResult.postValue(Result.failure(Exception("Login failed with code: ${response.code}")))
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                _loginResult.postValue(Result.failure(e))
            }
        }
    }
}
