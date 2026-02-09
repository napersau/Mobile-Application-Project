package com.example.fe.model

data class DocumentRequest(
    val title: String,
    val description: String?,
    val thumbnail: String?,
    val content: String,
    val category: DocCategory,
    val isPublished: Boolean = false
)

data class DocumentRequestValidation(
    private val title: String,
    private val description: String?,
    private val content: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (title.isBlank()) {
            errors.add("Tiêu đề không được để trống")
        }
        if (title.length > 255) {
            errors.add("Tiêu đề quá dài (tối đa 255 ký tự)")
        }
        if (description != null && description.length > 500) {
            errors.add("Mô tả ngắn không quá 500 ký tự")
        }
        if (content.isBlank()) {
            errors.add("Nội dung bài học không được để trống")
        }
        
        return errors
    }
}