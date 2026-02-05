package com.example.fe.repository

import com.example.fe.model.AuthenticationRequest
import com.example.fe.network.RetrofitClient


class AuthRepository {

    suspend fun login(username: String, password: String)
            = RetrofitClient.api.login(
        AuthenticationRequest(username, password)
    )
}