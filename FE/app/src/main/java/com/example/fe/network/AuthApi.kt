package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.AuthenticationRequest
import com.example.fe.model.AuthenticationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Primary login endpoint - matches backend @PostMapping("/api/v1/auth/token")
    @POST("api/v1/auth/token")
    suspend fun login(
        @Body request: AuthenticationRequest
    ): ApiResponse<AuthenticationResponse>

    // Introspect token endpoint
    @POST("api/v1/auth/introspect")
    suspend fun introspect(
        @Body request: Map<String, String>
    ): ApiResponse<Map<String, Any>>
}