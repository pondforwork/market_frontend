package com.example.marketbooking.model
import com.google.gson.annotations.SerializedName

class User(
    @SerializedName("id")
    val userId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("shop_name")
    val shopName: String,

    @SerializedName("user_booking_category_id")
    val bookingCategory: BookingCategory,
)


