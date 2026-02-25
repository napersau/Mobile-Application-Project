package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.VNPayResponse
import com.example.fe.repository.PaymentRepository
import kotlinx.coroutines.launch

class PaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentRepository()

    private val _paymentUrlLiveData = MutableLiveData<Result<VNPayResponse>>()
    val paymentUrlLiveData: LiveData<Result<VNPayResponse>> = _paymentUrlLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun createVnPayPayment(courseId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.createVnPayPayment(courseId)
                _paymentUrlLiveData.postValue(result)
            } catch (e: Exception) {
                _paymentUrlLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

