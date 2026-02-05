package com.example.fe.repository

import com.example.fe.model.AuthenticationRequest
import com.example.fe.network.RetrofitClient


class AuthRepository {

    suspend fun login(username: String, password: String)
            = RetrofitClient.authApi.login(
        AuthenticationRequest(username, password)
    )
}