package com.example.fe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fe.model.DocumentRequest
import com.example.fe.model.DocumentResponse
import com.example.fe.model.UserSession
import com.example.fe.repository.DocumentRepository
import kotlinx.coroutines.launch

class DocumentViewModel(private val repository: DocumentRepository) : ViewModel() {

    private val _documents = MutableLiveData<List<DocumentResponse>>()
    val documents: LiveData<List<DocumentResponse>> = _documents

    private val _selectedDocument = MutableLiveData<DocumentResponse>()
    val selectedDocument: LiveData<DocumentResponse> = _selectedDocument

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    // UI state cho permissions
    private val _canCreate = MutableLiveData<Boolean>()
    val canCreate: LiveData<Boolean> = _canCreate

    private val _canEdit = MutableLiveData<Boolean>()
    val canEdit: LiveData<Boolean> = _canEdit

    private val _canDelete = MutableLiveData<Boolean>()
    val canDelete: LiveData<Boolean> = _canDelete

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    init {
        updateUserPermissions()
    }

    fun updateUserPermissions() {
        _canCreate.value = UserSession.canCreate()
        _canEdit.value = UserSession.canEdit()
        _canDelete.value = UserSession.canDelete()
        _isAdmin.value = UserSession.isAdmin()
    }

    fun loadDocumentsByCategory(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getDocumentsByCategory(category).fold(
                onSuccess = { documents ->
                    _documents.value = documents
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Lỗi không xác định"
                }
            )
            _isLoading.value = false
        }
    }

    fun loadDocumentById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getDocumentById(id).fold(
                onSuccess = { document ->
                    _selectedDocument.value = document
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Không tìm thấy tài liệu"
                }
            )
            _isLoading.value = false
        }
    }

    fun createDocument(request: DocumentRequest) {
        if (!UserSession.canCreate()) {
            _error.value = "Bạn không có quyền tạo tài liệu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.createDocument(request).fold(
                onSuccess = { document ->
                    _success.value = "Tạo tài liệu thành công"
                    _selectedDocument.value = document
                    // Reload documents list
                    loadDocumentsByCategory()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Lỗi tạo tài liệu"
                }
            )
            _isLoading.value = false
        }
    }

    fun updateDocument(id: Long, request: DocumentRequest) {
        if (!UserSession.canEdit()) {
            _error.value = "Bạn không có quyền chỉnh sửa tài liệu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.updateDocument(id, request).fold(
                onSuccess = { document ->
                    _success.value = "Cập nhật tài liệu thành công"
                    _selectedDocument.value = document
                    // Reload documents list
                    loadDocumentsByCategory()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Lỗi cập nhật tài liệu"
                }
            )
            _isLoading.value = false
        }
    }

    fun deleteDocument(id: Long) {
        if (!UserSession.canDelete()) {
            _error.value = "Bạn không có quyền xóa tài liệu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.deleteDocument(id).fold(
                onSuccess = { message ->
                    _success.value = message
                    // Reload documents list
                    loadDocumentsByCategory()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Lỗi xóa tài liệu"
                }
            )
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = null
    }
}