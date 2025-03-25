package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class MakeHolidayResponse(
    @SerializedName("status")
    val status: String,
)