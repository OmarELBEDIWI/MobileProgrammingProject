package com.example.mobileprogrammingproject.viewmodels

import androidx.lifecycle.*
import com.example.mobileprogrammingproject.models.Feedback
import com.example.mobileprogrammingproject.models.FeedbackRequest
import com.example.mobileprogrammingproject.network.RetrofitClient
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {
    private val _feedback = MutableLiveData<List<Feedback>>()
    val feedback: LiveData<List<Feedback>> get() = _feedback

    private val _submissionResponse = MutableLiveData<SubmissionResponse>()
    val submissionResponse: LiveData<SubmissionResponse> get() = _submissionResponse

    fun fetchFeedback(courseId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFeedback(courseId)
                if (response.isSuccessful) {
                    _feedback.value = response.body()
                } else {
                    _feedback.value = emptyList()
                }
            } catch (e: Exception) {
                _feedback.value = emptyList()
            }
        }
    }

    fun fetchUserFeedback(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUserFeedback(userId)
                if (response.isSuccessful) {
                    _feedback.value = response.body()
                } else {
                    _feedback.value = emptyList()
                }
            } catch (e: Exception) {
                _feedback.value = emptyList()
            }
        }
    }

    fun submitFeedback(
        courseId: String,
        userId: String?,
        difficulty: Int,
        learningExperience: Int,
        comment: String?,
        anonymous: Boolean
    ): LiveData<SubmissionResponse> {
        viewModelScope.launch {
            try {
                val feedbackRequest = FeedbackRequest(
                    course_id = courseId,
                    difficulty = difficulty,
                    learning_experience = learningExperience,
                    comment = comment,
                    anonymous = anonymous,
                    user_id = if (!anonymous) userId else null
                )

                val response = RetrofitClient.apiService.submitFeedback(feedbackRequest)
                _submissionResponse.value = if (response.isSuccessful) {
                    SubmissionResponse(true, response.body()?.get("message") ?: "Feedback submitted")
                } else {
                    SubmissionResponse(false, response.errorBody()?.string() ?: "Submission failed")
                }
            } catch (e: Exception) {
                _submissionResponse.value = SubmissionResponse(false, "Network error")
            }
        }
        return submissionResponse
    }

    suspend fun addInstructorResponseByName(instructorName: String, courseName: String, responseText: String): SubmissionResponse {
        return try {
            val response = RetrofitClient.apiService.addInstructorResponseByName(
                mapOf(
                    "instructor_name" to instructorName,
                    "course_name" to courseName,
                    "response" to responseText
                )
            )
            if (response.isSuccessful) {
                SubmissionResponse(true, response.body()?.get("message") ?: "Response submitted")
            } else {
                SubmissionResponse(false, response.errorBody()?.string() ?: "Failed to submit")
            }
        } catch (e: Exception) {
            SubmissionResponse(false, "Network error")
        }
    }
}

data class SubmissionResponse(val success: Boolean, val message: String)
