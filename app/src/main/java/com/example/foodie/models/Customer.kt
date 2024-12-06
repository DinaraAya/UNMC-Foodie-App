package com.example.foodie.models

data class Customer(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: Long = System.currentTimeMillis()
)