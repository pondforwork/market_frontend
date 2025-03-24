package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class RequestBooking(
    @SerializedName("stalls_id")
    val stallId: Int,

    @SerializedName("booking_user_id")
    val bookingUserId: Int,

    @SerializedName("booking_category_id")
    val bookingCategoryId: Int,

    @SerializedName("price")
    val price: Int,
    )