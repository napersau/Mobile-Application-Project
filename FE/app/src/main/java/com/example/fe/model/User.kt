package com.example.fe.model

enum class UserRole {
    USER,
    ADMIN
}

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val role: UserRole,
    val token: String? = null
)

object UserSession {
    var currentUser: User? = null
        private set

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun isLoggedIn(): Boolean = currentUser != null

    fun isAdmin(): Boolean = currentUser?.role == UserRole.ADMIN

    fun canEdit(): Boolean = isAdmin()

    fun canDelete(): Boolean = isAdmin()

    fun canCreate(): Boolean = isAdmin()
}