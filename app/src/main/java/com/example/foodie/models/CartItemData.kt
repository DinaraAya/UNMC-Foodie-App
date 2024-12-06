package com.example.foodie.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItemData(
    val itemName: String = "",
    val quantity: Int = 0 ,
    val price: Double= 0.0
) : Parcelable