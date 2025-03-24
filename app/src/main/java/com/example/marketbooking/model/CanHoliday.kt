package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class CanHoliday(
    @SerializedName("status")
    val status: String,
)
