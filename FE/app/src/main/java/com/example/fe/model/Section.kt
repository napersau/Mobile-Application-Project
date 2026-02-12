package com.example.fe.model

data class Section(
    val id: Long,
    val title: String,
    val orderIndex: Int,
    val course: Course? = null,
    val lessons: List<Lesson>? = null
)
