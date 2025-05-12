package com.example.mobileprogrammingproject.network

import com.example.mobileprogrammingproject.models.Course
import com.example.mobileprogrammingproject.models.Feedback
import com.example.mobileprogrammingproject.models.FeedbackRequest
import com.example.mobileprogrammingproject.models.LoginResponse
import retrofit2.Response
import retrofit2.http.*

data class AuthResponse(val message: String, val userId: String? = null)
data class Instructor(val username: String)

interface ApiService {
    @POST("register")
    suspend fun register(@Body user: Map<String, String>): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @GET("courses")
    suspend fun getCourses(@Query("instructor_name") instructorName: String? = null): Response<List<Course>>

    @GET("instructors")
    suspend fun getInstructors(): Response<List<Instructor>>

    @POST("add-course")
    suspend fun addCourse(@Body course: Map<String, String>): Response<Map<String, String>>

    @POST("feedback")
    suspend fun submitFeedback(@Body feedback: FeedbackRequest): Response<Map<String, String>>

    @GET("feedback/{course_id}")
    suspend fun getFeedback(@Path("course_id") courseId: String): Response<List<Feedback>>

    @GET("feedback/user/{user_id}")
    suspend fun getUserFeedback(@Path("user_id") userId: String): Response<List<Feedback>>

    @POST("feedback/{feedback_id}/response")
    suspend fun addInstructorResponse(
        @Path("feedback_id") feedbackId: String,
        @Body response: Map<String, String>
    ): Response<Map<String, String>>

    @GET("feedback/instructor/{instructor_name}")
    suspend fun getFeedbackByInstructor(@Path("instructor_name") instructorName: String): Response<List<Feedback>>

    @POST("feedback/response-by-instructor")
    suspend fun addInstructorResponseByName(@Body request: Map<String, String>): Response<Map<String, String>>
}
