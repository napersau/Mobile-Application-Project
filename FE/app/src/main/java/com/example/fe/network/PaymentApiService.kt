package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.VNPayResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PaymentApiService {

    @GET("api/v1/payment/vn-pay")
    suspend fun createVnPayPayment(
        @Query("courseId") courseId: Long,
        @Query("platform") platform: String = "android"
    ): Response<ApiResponse<VNPayResponse>>

    @GET("api/v1/payment/vn-pay-callback")
    suspend fun vnPayCallback(
        @Query("vnp_ResponseCode") responseCode: String,
        @Query("vnp_TxnRef") txnRef: String
    ): Response<ApiResponse<VNPayResponse>>
}

