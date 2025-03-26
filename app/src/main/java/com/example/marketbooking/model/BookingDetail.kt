package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class BookingDetail(
    @SerializedName("name")
    val stallName: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("price")
    val price: String,
)