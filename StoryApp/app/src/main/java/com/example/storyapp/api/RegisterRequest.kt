package com.example.storyapp.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)