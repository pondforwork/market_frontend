package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class BookingDetail(
    @SerializedName("stall_name")
    val stallsId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String,
)