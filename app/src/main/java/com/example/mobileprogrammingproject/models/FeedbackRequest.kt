package com.example.mobileprogrammingproject.models

data class FeedbackRequest(
    val course_id: String,
    val difficulty: Int,
    val learning_experience: Int,
    val comment: String? = null,
    val anonymous: Boolean = true,
    val user_id: String? = null,
    val username: String? = null
)
