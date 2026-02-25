package com.example.fe.model

import java.time.Instant

data class VNPayResponse(
    val code: String,
    val message: String,
    val paymentUrl: String
)

data class OrderResponse(
    val id: Long,
    val username: String?,
    val courseTitle: String?,
    val vnpTxnRef: String?,
    val amount: Double?,
    val status: String?,
    val createdDate: Instant?,
    val payDate: Instant?
)

