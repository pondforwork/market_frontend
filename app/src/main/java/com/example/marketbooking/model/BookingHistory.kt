package com.example.marketbooking.model

import com.google.gson.annotations.SerializedName

data class BookingHistory(
    @SerializedName("id")
    val id: Int,

    @SerializedName("stalls_id")
    val stallsId: Int,

    @SerializedName("booking_user_id")
    val bookingUserId: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String, // แนะนำให้ใช้ String หรือเปลี่ยนเป็น Date ถ้าคุณแปลงมัน

    @SerializedName("stall_name")
    val stallName: String
)
