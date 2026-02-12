package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.Course
import com.example.fe.model.CourseRequest
import com.example.fe.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CourseRepository(application.applicationContext)

    private val _coursesLiveData = MutableLiveData<Result<List<Course>>>()
    val coursesLiveData: LiveData<Result<List<Course>>> = _coursesLiveData

    private val _courseDetailLiveData = MutableLiveData<Result<Course>>()
    val courseDetailLiveData: LiveData<Result<Course>> = _courseDetailLiveData

    private val _createCourseLiveData = MutableLiveData<Result<Course>>()
    val createCourseLiveData: LiveData<Result<Course>> = _createCourseLiveData

    private val _deleteCourseLiveData = MutableLiveData<Result<Unit>>()
    val deleteCourseLiveData: LiveData<Result<Unit>> = _deleteCourseLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllCourses() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllCourses()
                _coursesLiveData.postValue(result)
            } catch (e: Exception) {
                _coursesLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getCourseById(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getCourseById(id)
                _courseDetailLiveData.postValue(result)
            } catch (e: Exception) {
                _courseDetailLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createCourse(request: CourseRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.createCourse(request)
                _createCourseLiveData.postValue(result)
            } catch (e: Exception) {
                _createCourseLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun publishCourse(id: Long) {
        viewModelScope.launch {
            try {
                val result = repository.publishCourse(id)
                if (result.isSuccess) {
                    // Refresh course detail
                    getCourseById(id)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteCourse(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.deleteCourse(id)
                _deleteCourseLiveData.postValue(result)
            } catch (e: Exception) {
                _deleteCourseLiveData.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

