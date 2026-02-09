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
                Log.d("AuthViewModel", "Response received - code: ${response.code}, message: ${response.message}")

                if (response.code == 1000) {
                    Log.d("AuthViewModel", "Login successful, posting success")
                    _loginResult.postValue(Result.success(response.result))
                } else {
                    val errorMsg = "Đăng nhập thất bại: ${response.message ?: "Lỗi không xác định"}"
                    Log.e("AuthViewModel", errorMsg)
                    _loginResult.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = try {
                    e.response()?.errorBody()?.string()
                } catch (ex: Exception) {
                    null
                }
                Log.e("AuthViewModel", "HTTP ${e.code()} - Error body: $errorBody")

                val errorMsg = when (e.code()) {
                    401 -> "Sai tên đăng nhập hoặc mật khẩu"
                    404 -> "Không tìm thấy API endpoint. Kiểm tra: identity/auth/login"
                    500 -> "Lỗi server. Vui lòng thử lại sau"
                    else -> "Lỗi kết nối: HTTP ${e.code()}"
                }
                Log.e("AuthViewModel", "HTTP Exception: ${e.code()} - ${e.message()}", e)
                _loginResult.postValue(Result.failure(Exception(errorMsg)))
            } catch (e: java.net.UnknownHostException) {
                Log.e("AuthViewModel", "Cannot connect to server", e)
                _loginResult.postValue(Result.failure(Exception("Không thể kết nối đến server. Kiểm tra kết nối mạng hoặc đảm bảo server đang chạy")))
            } catch (e: java.net.ConnectException) {
                Log.e("AuthViewModel", "Connection refused", e)
                _loginResult.postValue(Result.failure(Exception("Server không phản hồi. Đảm bảo backend đang chạy tại http://10.0.2.2:8080")))
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                _loginResult.postValue(Result.failure(Exception("Lỗi không xác định: ${e.message}")))
            }
        }
    }
}
