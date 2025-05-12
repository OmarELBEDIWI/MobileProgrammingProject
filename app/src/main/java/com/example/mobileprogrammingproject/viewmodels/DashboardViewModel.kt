package com.example.mobileprogrammingproject.viewmodels

import androidx.lifecycle.*
import com.example.mobileprogrammingproject.models.Course
import com.example.mobileprogrammingproject.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> get() = _courses

    fun fetchCourses(instructorName: String? = null) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCourses(instructorName)
                if (response.isSuccessful) {
                    _courses.value = response.body()
                } else {
                    _courses.value = emptyList()
                }
            } catch (e: Exception) {
                _courses.value = emptyList()
            }
        }
    }

    fun login(username: String, password: String): LiveData<LoginResponse> = liveData {
        try {
            val response = RetrofitClient.apiService.login(
                mapOf("username" to username, "password" to password)
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(LoginResponse(
                    success = true,
                    message = body.message,
                    userId = body.userId,
                    role = body.role,
                    username = body.username // Map the username from the API response
                ))
            } else {
                emit(LoginResponse(
                    success = false,
                    message = body?.message ?: "Login failed",
                    userId = null,
                    role = null,
                    username = null
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(LoginResponse(
                success = false,
                message = "Network error",
                userId = null,
                role = null,
                username = null
            ))
        }
    }

    fun register(username: String, password: String, role: String): LiveData<LoginResponse> = liveData {
        try {
            val response = RetrofitClient.apiService.register(
                mapOf("username" to username, "password" to password, "role" to role)
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(LoginResponse(
                    success = true,
                    message = body.message,
                    userId = null,
                    role = null,
                    username = username
                ))
            } else {
                emit(LoginResponse(
                    success = false,
                    message = "Registration failed",
                    userId = null,
                    role = null,
                    username = null
                ))
            }
        } catch (e: Exception) {
            emit(LoginResponse(
                success = false,
                message = "Network error",
                userId = null,
                role = null,
                username = null
            ))
        }
    }
}

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: String?,
    val role: String? = "student",
    val isInstructor: Boolean = false,
    val username: String? = null // Added username field
)
