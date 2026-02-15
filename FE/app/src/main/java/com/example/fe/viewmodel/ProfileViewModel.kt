package com.example.fe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.UserResponse
import com.example.fe.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _userInfo = MutableLiveData<Result<UserResponse>>()
    val userInfo: LiveData<Result<UserResponse>> = _userInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getMyInfo() {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                Log.d("ProfileViewModel", "=== FETCHING USER INFO ===")
                Log.d("ProfileViewModel", "URL: http://10.0.2.2:8080/api/v1/users/my-info")

                val response = repository.getMyInfo()
                Log.d("ProfileViewModel", "Response received - code: ${response.code}, message: ${response.message}")

                if (response.code == 1000 && response.result != null) {
                    Log.d("ProfileViewModel", "User info retrieved successfully")
                    Log.d("ProfileViewModel", "User: ${response.result.fullName} (${response.result.username})")
                    _userInfo.postValue(Result.success(response.result))
                } else {
                    val errorMsg = "Không thể lấy thông tin người dùng: ${response.message}"
                    Log.e("ProfileViewModel", errorMsg)
                    _userInfo.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = try {
                    e.response()?.errorBody()?.string()
                } catch (_: Exception) {
                    null
                }
                Log.e("ProfileViewModel", "=== HTTP ERROR ===")
                Log.e("ProfileViewModel", "Status Code: ${e.code()}")
                Log.e("ProfileViewModel", "Error Body: '$errorBody'")

                val errorMsg = when (e.code()) {
                    401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại"
                    403 -> "Không có quyền truy cập"
                    404 -> "Không tìm thấy thông tin người dùng"
                    500 -> "Lỗi máy chủ"
                    else -> "Lỗi kết nối: ${e.message()}"
                }
                _userInfo.postValue(Result.failure(Exception(errorMsg)))
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("ProfileViewModel", "=== TIMEOUT ERROR ===", e)
                _userInfo.postValue(Result.failure(Exception("Hết thời gian chờ. Vui lòng thử lại")))
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "=== UNEXPECTED ERROR ===", e)
                _userInfo.postValue(Result.failure(Exception("Đã xảy ra lỗi: ${e.message}")))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

