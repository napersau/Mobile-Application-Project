package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.AuthenticationRequest
import com.example.fe.model.AuthenticationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/token")
    suspend fun login(
        @Body request: AuthenticationRequest
    ): ApiResponse<AuthenticationResponse>
}