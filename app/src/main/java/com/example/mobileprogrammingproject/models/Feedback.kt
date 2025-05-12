package com.example.mobileprogrammingproject.models

data class Feedback(
    val id: String? = null,
    val course_id: String,
    val user_id: String? = null,
    val difficulty: Int,
    val learning_experience: Int,
    val comment: String?,
    val instructor_response: String? = null,
    val anonymous: Boolean = true,
    val username: String? = null
)

