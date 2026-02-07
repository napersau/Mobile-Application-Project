package com.example.fe.network

import android.content.Context
import android.util.Log
import com.example.fe.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        return try {
            val token = TokenManager.getAccessToken(context)
            
            val newRequest = if (!token.isNullOrEmpty()) {
                Log.d("AuthInterceptor", "Adding token to request")
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.d("AuthInterceptor", "No token available, proceeding without auth")
                originalRequest
            }
            
            chain.proceed(newRequest)
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error in interceptor", e)
            chain.proceed(originalRequest)
        }
    }
}
