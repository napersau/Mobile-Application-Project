package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.OrderResponse
import com.example.fe.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository()

    private val _ordersLiveData = MutableLiveData<Result<List<OrderResponse>>>()
    val ordersLiveData: LiveData<Result<List<OrderResponse>>> = _ordersLiveData

    private val _processPaymentLiveData = MutableLiveData<Result<Unit>>()
    val processPaymentLiveData: LiveData<Result<Unit>> = _processPaymentLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllOrders() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllOrders()
                _ordersLiveData.postValue(result)
            } catch (e: Exception) {
                _ordersLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun processSuccessfulPayment(vnpTxnRef: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.processSuccessfulPayment(vnpTxnRef)
                _processPaymentLiveData.postValue(result)
            } catch (e: Exception) {
                _processPaymentLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

