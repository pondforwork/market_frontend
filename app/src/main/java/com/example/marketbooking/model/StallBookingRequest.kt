package com.example.marketbooking.model

import java.time.LocalDateTime

data class StallBookingRequest(
    val stallsId: Int,
    val bookingUserId: Int,
    val status: String,
    val aprDclBy: String,
    val bookingCategoryId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val updatedBy : Int

)