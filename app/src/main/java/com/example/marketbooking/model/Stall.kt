package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class Stall(
    @SerializedName("stall_id")
    val stallId: Int,

    @SerializedName("stall_name")
    val stallName: String,

    @SerializedName("line_name")
    val lineName: String,

    @SerializedName("line_sequence")
    val lineSequence: Int,

    @SerializedName("booking_status")
    val bookingStatus: String,

    @SerializedName("total_days")
    val totalDays: Int,

    @SerializedName("available_days")
    val availableDays: String,

    @SerializedName("price")
    val price: String
)