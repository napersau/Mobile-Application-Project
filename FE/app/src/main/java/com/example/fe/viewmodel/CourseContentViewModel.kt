package com.example.fe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fe.model.Course
import com.example.fe.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseContentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CourseRepository()

    private val _courseDetail = MutableLiveData<Result<Course>>()
    val courseDetail: LiveData<Result<Course>> = _courseDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCourse(id: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getCourseById(id)
                _courseDetail.postValue(result)
            } catch (e: Exception) {
                _courseDetail.postValue(Result.failure(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

