package com.example.fe.repository

import com.example.fe.network.RetrofitClient

class AnalyticsRepository {

    suspend fun getUserGamification() =
        RetrofitClient.analyticsApi.getUserGamification()

    suspend fun getStudyStats(days: Int = 7) =
        RetrofitClient.analyticsApi.getStudyStats(days)
}

