package com.example.mobileprogrammingproject.utils

object ValidationUtils {
    fun isValidCredentials(username: String, password: String): Boolean {
        return username.isNotBlank() && password.length >= 6
    }

    fun isValidFeedback(difficulty: Int, learningExperience: Int): Boolean {
        return difficulty in 1..5 && learningExperience in 1..5
    }
}
