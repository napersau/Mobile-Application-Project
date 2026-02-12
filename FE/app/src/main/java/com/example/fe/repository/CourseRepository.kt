package com.example.fe.repository

import android.content.Context
import com.example.fe.model.Course
import com.example.fe.model.CourseRequest
import com.example.fe.network.CourseApiService
import com.example.fe.network.RetrofitClient

class CourseRepository(private val context: Context) {

    private val api: CourseApiService = RetrofitClient.getInstance(context).create(CourseApiService::class.java)

    suspend fun createCourse(request: CourseRequest): Result<Course> {
        return try {
            val response = api.createCourse(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000 && body.result != null) {
                    Result.success(body.result)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to create course"))
                }
            } else {
                Result.failure(Exception("Failed to create course"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCourseById(id: Long): Result<Course> {
        return try {
            val response = api.getCourseById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000 && body.result != null) {
                    Result.success(body.result)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to get course"))
                }
            } else {
                Result.failure(Exception("Failed to get course"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCourses(): Result<List<Course>> {
        return try {
            val response = api.getAllCourses()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000) {
                    Result.success(body.result ?: emptyList())
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to get courses"))
                }
            } else {
                Result.failure(Exception("Failed to get courses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publishCourse(id: Long): Result<Course> {
        return try {
            val response = api.publishCourse(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000 && body.result != null) {
                    Result.success(body.result)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to publish course"))
                }
            } else {
                Result.failure(Exception("Failed to publish course"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCourse(id: Long): Result<Unit> {
        return try {
            val response = api.deleteCourse(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 1000) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Failed to delete course"))
                }
            } else {
                Result.failure(Exception("Failed to delete course"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

