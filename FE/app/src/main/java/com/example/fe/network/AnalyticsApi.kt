package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.DailyStudyStatResponse
import com.example.fe.model.UserGamificationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnalyticsApi {

    @GET("api/v1/analytics")
    suspend fun getUserGamification(): ApiResponse<UserGamificationResponse>

    @GET("api/v1/analytics/stats")
    suspend fun getStudyStats(
        @Query("days") days: Int = 7
    ): ApiResponse<List<DailyStudyStatResponse>>
}

