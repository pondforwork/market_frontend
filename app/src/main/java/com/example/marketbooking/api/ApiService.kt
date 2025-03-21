package com.example.marketbooking.api

import com.example.marketbooking.model.Stall
import com.example.marketbooking.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("available_stalls")
    suspend fun getAvailableStalls(): Response<List<Stall>>

    @POST("register")
    suspend fun register(@Body user: User): Response<User>
}
