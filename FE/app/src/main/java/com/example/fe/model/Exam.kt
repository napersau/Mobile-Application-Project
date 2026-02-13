package com.example.fe.model

// Response models matching backend
data class ExamResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val duration: Int?, // minutes
    val type: ExamType,
    val totalQuestions: Int?,
    val questionGroups: List<QuestionGroupResponse>? = null
)

data class QuestionGroupResponse(
    val id: Long,
    val type: PartType,
    val content: String?, // HTML content for Part 6, 7
    val audioUrl: String?, // Audio file for Part 1,2,3,4
    val imageUrl: String?, // Image file for Part 1
    val questions: List<QuestionResponse>? = null
)

data class QuestionResponse(
    val id: Long,
    val questionNumber: Int, // Question number (101, 102...)
    val text: String, // Question text
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String?, // Part 2 may have null
    val correctAnswer: String, // Only "A", "B", "C", or "D"
    val explanation: String? // Detailed explanation
)

// Request models
data class ExamRequest(
    val title: String,
    val description: String?,
    val duration: Int?,
    val type: ExamType,
    val totalQuestions: Int?,
    val questionGroups: List<QuestionGroupRequest>? = null,
    val questionGroupsIds: List<Long>? = null
)

data class QuestionGroupRequest(
    val type: PartType,
    val content: String?,
    val audioUrl: String?,
    val imageUrl: String?,
    val questions: List<QuestionRequest>? = null
)

data class QuestionRequest(
    val questionNumber: Int,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String?,
    val correctAnswer: String,
    val explanation: String?
)

// Enums
enum class ExamType {
    TOEIC_FULL_TEST,  // Full TOEIC test (200 questions)
    TOEIC_MINI_TEST,  // Mini test (50-100 questions)
    IELTS_ACADEMIC,   // IELTS Academic
    IELTS_GENERAL,    // IELTS General
    MOCK_TEST         // General mock test
}

enum class PartType {
    PART_1, // Photographs
    PART_2, // Question-Response
    PART_3, // Conversations
    PART_4, // Short Talks
    PART_5, // Incomplete Sentences
    PART_6, // Text Completion
    PART_7  // Reading Comprehension
}

// UI models for exam taking
data class UserAnswer(
    val questionId: Long,
    val selectedAnswer: String? // "A", "B", "C", "D" or null
)

// Local exam result for UI (before submitting to backend)
data class ExamResult(
    val examId: Long,
    val examTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val userAnswers: List<UserAnswer>,
    val timeTaken: Long, // milliseconds
    val score: Float, // percentage
    val completedAt: Long // timestamp
)

// Backend API models for ExamResult
data class ExamResultRequest(
    val score: Int,
    val listeningScore: Int?,
    val readingScore: Int?,
    val correctCount: Int, // Số câu đúng
    val submitTime: String, // ISO 8601 format
    val timeTaken: Int, // Thời gian làm bài (giây)
    val examId: Long,
    val examResultDetailRequestList: List<ExamResultDetailRequest>
)

data class ExamResultDetailRequest(
    val selectedOption: String?, // "A", "B", "C", "D" or null
    val isCorrect: Boolean,
    val questionId: Long // ID của câu hỏi
)

data class ExamResultResponse(
    val id: Long,
    val score: Int,
    val listeningScore: Int?,
    val readingScore: Int?,
    val correctCount: Int,
    val submitTime: String,
    val timeTaken: Int,
    val user: UserResponse?,
    val exam: ExamResponse?,
    val details: List<ExamResultDetailResponse>?
)

data class ExamResultDetailResponse(
    val id: Long,
    val selectedOption: String?,
    val isCorrect: Boolean,
    val question: QuestionResponse?
)

