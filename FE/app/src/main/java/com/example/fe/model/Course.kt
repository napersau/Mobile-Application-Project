package com.example.fe.model

data class Course(
    val id: Long,
    val title: String,
    val description: String?,
    val content: String?,
    val price: Double?,
    val imageUrl: String?,
    val level: String?, // Beginner, Intermediate, Advanced
    val totalStudents: Int = 0,
    val isPublished: Boolean = false,
    val sections: List<Section>? = null
)

data class CourseRequest(
    val title: String,
    val description: String?,
    val price: Double?,
    val level: String?
)

