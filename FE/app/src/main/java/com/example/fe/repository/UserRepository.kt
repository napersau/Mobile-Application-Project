package com.example.fe.repository

import com.example.fe.model.UserRequest
import com.example.fe.network.RetrofitClient

class UserRepository {

    suspend fun register(request: UserRequest) =
        RetrofitClient.userApi.register(request)
}