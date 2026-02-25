package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.OrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApiService {

    @POST("api/v1/order/process-payment")
    suspend fun processSuccessfulPayment(
        @Query("vnpTxnRef") vnpTxnRef: String
    ): Response<ApiResponse<Void>>

    @GET("api/v1/order/list")
    suspend fun getAllOrders(): Response<ApiResponse<List<OrderResponse>>>
}

