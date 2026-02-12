package com.example.fe.repository

import android.util.Log
import com.example.fe.model.*
import com.example.fe.network.DocumentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DocumentRepository(private val apiService: DocumentApi) {

    companion object {
        private const val TAG = "DocumentRepository"
    }

    suspend fun getDocumentsByCategory(category: String): Result<List<DocumentResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val apiResponse = apiService.getDocumentsByCategory(category)

                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting documents by category", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getDocumentById(id: Long): Result<DocumentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val apiResponse = apiService.getDocumentById(id)
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting document by id", e)
                Result.failure(e)
            }
        }
    }

    suspend fun createDocument(request: DocumentRequest): Result<DocumentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Kiểm tra quyền admin
                if (!UserSession.canCreate()) {
                    return@withContext Result.failure(Exception("Bạn không có quyền tạo tài liệu"))
                }

                // Validate input
                val validation = DocumentRequestValidation(request.title, request.description, request.content)
                val errors = validation.validate()
                if (errors.isNotEmpty()) {
                    return@withContext Result.failure(Exception(errors.joinToString("\n")))
                }

                val apiResponse = apiService.createDocument(request)
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating document", e)
                Result.failure(e)
            }
        }
    }

    suspend fun updateDocument(id: Long, request: DocumentRequest): Result<DocumentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Kiểm tra quyền admin
                if (!UserSession.canEdit()) {
                    return@withContext Result.failure(Exception("Bạn không có quyền chỉnh sửa tài liệu"))
                }

                // Validate input
                val validation = DocumentRequestValidation(request.title, request.description, request.content)
                val errors = validation.validate()
                if (errors.isNotEmpty()) {
                    return@withContext Result.failure(Exception(errors.joinToString("\n")))
                }

                val apiResponse = apiService.updateDocument(id, request)
                if (apiResponse.code == 1000 && apiResponse.result != null) {
                    Result.success(apiResponse.result)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating document", e)
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDocument(id: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Kiểm tra quyền admin
                if (!UserSession.canDelete()) {
                    return@withContext Result.failure(Exception("Bạn không có quyền xóa tài liệu"))
                }

                val apiResponse = apiService.deleteDocument(id)
                if (apiResponse.code == 1000) {
                    Result.success(apiResponse.message)
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting document", e)
                Result.failure(e)
            }
        }
    }
}