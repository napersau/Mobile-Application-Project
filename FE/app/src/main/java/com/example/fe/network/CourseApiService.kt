package com.example.fe.network

import com.example.fe.model.ApiResponse
import com.example.fe.model.Course
import com.example.fe.model.CourseRequest
import retrofit2.Response
import retrofit2.http.*

interface CourseApiService {

    @POST("api/v1/courses")
    suspend fun createCourse(
        @Body request: CourseRequest
    ): Response<ApiResponse<Course>>

    @GET("api/v1/courses/{id}")
    suspend fun getCourseById(
        @Path("id") id: Long
    ): Response<ApiResponse<Course>>

    @GET("api/v1/courses/all")
    suspend fun getAllCourses(): Response<ApiResponse<List<Course>>>

    @PUT("api/v1/courses/{id}")
    suspend fun publishCourse(
        @Path("id") id: Long
    ): Response<ApiResponse<Course>>

    @DELETE("api/v1/courses/{id}")
    suspend fun deleteCourse(
        @Path("id") id: Long
    ): Response<ApiResponse<Void>>
}

