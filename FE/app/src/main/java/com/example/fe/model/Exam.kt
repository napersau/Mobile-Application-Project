package com.example.fe.model

data class Exam(
    val id: Long,
    val title: String,
    val description: String?,
    val type: ExamType,
    val duration: Int?, // minutes
    val totalQuestions: Int?,
    val questionGroups: List<QuestionGroup>? = null
)

enum class ExamType {
    TOEIC,
    IELTS,
    TOEFL,
    PRACTICE,
    OTHER
}

data class QuestionGroup(
    val id: Long,
    val title: String?,
    val content: String?,
    val orderIndex: Int,
    val questions: List<Question>? = null
)

data class Question(
    val id: Long,
    val content: String,
    val orderIndex: Int,
    val answers: List<Answer>? = null
)

data class Answer(
    val id: Long,
    val content: String,
    val isCorrect: Boolean = false
)

