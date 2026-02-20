package com.example.fe.model

import java.time.Instant

data class UserGamificationResponse(
    val userId: Long,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastLearnDate: Instant?,
    val learnedToday: Boolean = false
)

