package com.example.fe.network

import android.content.Context
import android.util.Log
import com.example.fe.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = TokenManager.getToken(context)

        val newRequest = if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "Adding token to request: ${originalRequest.url}")
            Log.d("AuthInterceptor", "Token: ${token.take(20)}...")
            Log.d("AuthInterceptor", "Token length: ${token.length}")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            Log.e("AuthInterceptor", "❌ NO TOKEN AVAILABLE for: ${originalRequest.url}")
            Log.e("AuthInterceptor", "User needs to login first!")
            originalRequest
        }

        return try {
            val response = chain.proceed(newRequest)
            if (response.code == 401) {
                Log.e("AuthInterceptor", "❌ 401 Unauthorized response from: ${originalRequest.url}")
                Log.e("AuthInterceptor", "Token was ${if (token.isNullOrEmpty()) "NOT" else ""} included in request")
            }
            response
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthInterceptor", "⏱️ Timeout for: ${originalRequest.url} - ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "❌ Error in interceptor for: ${originalRequest.url}", e)
            throw e // Re-throw to let caller handle
        }
    }
}
