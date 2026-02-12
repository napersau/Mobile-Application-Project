package com.example.fe.network

import com.example.fe.model.*
import retrofit2.http.*

interface DocumentApi {


    @GET("api/v1/documents/category")
    suspend fun getDocumentsByCategory(
        @Query("category") category: String
    ): ApiResponse<List<DocumentResponse>>

    @GET("api/v1/documents/{id}")
    suspend fun getDocumentById(
        @Path("id") id: Long
    ): ApiResponse<DocumentResponse>

    @POST("api/v1/documents")
    suspend fun createDocument(
        @Body request: DocumentRequest
    ): ApiResponse<DocumentResponse>

    @PUT("api/v1/documents/{id}")
    suspend fun updateDocument(
        @Path("id") id: Long,
        @Body request: DocumentRequest
    ): ApiResponse<DocumentResponse>

    @DELETE("api/v1/documents/{id}")
    suspend fun deleteDocument(
        @Path("id") id: Long
    ): ApiResponse<Void>
}
