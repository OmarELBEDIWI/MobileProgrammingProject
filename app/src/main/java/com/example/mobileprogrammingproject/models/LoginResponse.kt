package com.example.mobileprogrammingproject.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("role")
    val role: String? = "student",
    @SerializedName("username")
    val username: String? = null
)
