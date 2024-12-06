package com.example.foodie.models

import com.google.firebase.Timestamp

data class Order (
    val userId: String = "",
    val stallId: String = "",
    val stallName: String = "",
    val totalItems: Int = 0,
    val totalCost: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    val status: String = "Order Placed",
    val items: List<CartItemData> = emptyList(),
    val orderNumber: Int = 0
)