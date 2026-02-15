package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.ChatMessage
import com.example.fe.repository.AIRepository
import kotlinx.coroutines.launch

class AIViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AIRepository(application.applicationContext)

    private val _chatMessages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val chatMessages: LiveData<MutableList<ChatMessage>> = _chatMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _translationResult = MutableLiveData<String>()
    val translationResult: LiveData<String> = _translationResult

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Add user message
        val userMessage = ChatMessage(
            message = message,
            isUser = true
        )
        val currentMessages = _chatMessages.value ?: mutableListOf()
        currentMessages.add(userMessage)
        _chatMessages.value = currentMessages

        // Call API
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.chat(message)
                result.onSuccess { aiResponse ->
                    // Add AI response
                    val aiMessage = ChatMessage(
                        message = aiResponse,
                        isUser = false
                    )
                    val messages = _chatMessages.value ?: mutableListOf()
                    messages.add(aiMessage)
                    _chatMessages.postValue(messages)
                }
                result.onFailure { error ->
                    val errorMsg = when {
                        error.message?.contains("timeout", ignoreCase = true) == true ->
                            "Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại hoặc đặt câu hỏi ngắn gọn hơn."
                        error.message?.contains("401") == true || error.message?.contains("Unauthorized") == true ->
                            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                        else -> "Xin lỗi, tôi gặp lỗi: ${error.message}"
                    }
                    _errorMessage.postValue(errorMsg)
                    // Add error message
                    val errorMessage = ChatMessage(
                        message = errorMsg,
                        isUser = false
                    )
                    val messages = _chatMessages.value ?: mutableListOf()
                    messages.add(errorMessage)
                    _chatMessages.postValue(messages)
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại."
                    else -> e.message ?: "Unknown error"
                }
                _errorMessage.postValue(errorMsg)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun translate(text: String) {
        if (text.isBlank()) {
            _errorMessage.value = "Vui lòng nhập văn bản cần dịch"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.translate(text)
                result.onSuccess { translation ->
                    _translationResult.postValue(translation)
                }
                result.onFailure { error ->
                    val errorMsg = when {
                        error.message?.contains("timeout", ignoreCase = true) == true ->
                            "Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại."
                        error.message?.contains("401") == true || error.message?.contains("Unauthorized") == true ->
                            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                        else -> error.message ?: "Unknown error"
                    }
                    _errorMessage.postValue(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại."
                    else -> e.message ?: "Unknown error"
                }
                _errorMessage.postValue(errorMsg)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = mutableListOf()
    }
}

