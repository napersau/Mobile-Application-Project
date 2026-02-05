package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.UserRequest
import com.example.fe.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("api/v1/users")
    suspend fun register(
        @Body request: UserRequest
    ): ApiResponse<UserResponse>
}
