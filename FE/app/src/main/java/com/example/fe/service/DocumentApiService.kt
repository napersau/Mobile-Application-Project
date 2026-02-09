package com.example.fe.service

import com.example.fe.model.*
import retrofit2.Response
import retrofit2.http.*

interface DocumentApiService {
    
    @GET("/api/v1/documents/category")
    suspend fun getDocumentsByCategory(
        @Query("category") category: String?
    ): Response<ApiResponse<List<DocumentResponse>>>

    @GET("/api/v1/documents/{id}")
    suspend fun getDocumentById(
        @Path("id") id: Long
    ): Response<ApiResponse<DocumentResponse>>

    @POST("/api/v1/documents")
    suspend fun createDocument(
        @Body request: DocumentRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<DocumentResponse>>

    @PUT("/api/v1/documents/{id}")
    suspend fun updateDocument(
        @Path("id") id: Long,
        @Body request: DocumentRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<DocumentResponse>>

    @DELETE("/api/v1/documents/{id}")
    suspend fun deleteDocument(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Void>>
}