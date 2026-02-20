package com.example.fe.model

data class DailyStudyStatResponse(
    val date: String,
    val hasLearned: Boolean,
    val durationSeconds: Long
)
