package com.example.fe

import android.app.Application
import com.example.fe.network.RetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Retrofit with application context
        RetrofitClient.initialize(this)
    }
}
