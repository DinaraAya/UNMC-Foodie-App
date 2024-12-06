package com.example.foodie.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stalls(
    var id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val operatingHours: String = "",
    val operatingDays: String = "",
    val closedDays: String = "",
    @field:JvmField
    val isOutdoor: Boolean = false,
) : Parcelable

@Parcelize
data class MenuCategory(
    val categoryName: String = "",
) : Parcelable

@Parcelize
data class MyMenuItem(
    val itemName: String = "",
    val itemPrice: String = "",
    val imageUrl2: String? = null,
) : Parcelable