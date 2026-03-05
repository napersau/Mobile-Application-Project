package com.example.fe.repository

import com.example.fe.model.AuthenticationRequest
import com.example.fe.model.GoogleTokenRequest
import com.example.fe.network.RetrofitClient


class AuthRepository {

    suspend fun login(username: String, password: String)
            = RetrofitClient.authApi.login(
        AuthenticationRequest(username, password)
    )

    suspend fun loginWithGoogle(idToken: String)
            = RetrofitClient.authApi.loginWithGoogle(
        GoogleTokenRequest(idToken)
    )
}