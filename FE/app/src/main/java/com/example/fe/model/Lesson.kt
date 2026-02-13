package com.example.fe.model

data class Lesson(
    val id: Long,
    val title: String,
    val orderIndex: Int,
    val isFree: Boolean = false,
    val type: LessonType,
    val videoUrl: String? = null,
    val duration: Int? = null, // in seconds
    val document: DocumentResponse? = null,
    val deck: DecksResponse? = null,
    val exam: ExamResponse? = null
)

enum class LessonType {
    VIDEO,      // Bài giảng video
    DOCUMENT,   // Bài đọc
    FLASHCARD,  // Bài ôn từ vựng
    EXAM        // Bài kiểm tra
}

