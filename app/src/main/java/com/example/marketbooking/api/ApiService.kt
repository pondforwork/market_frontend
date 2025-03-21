package com.example.marketbooking.api

import com.example.marketbooking.model.Stall
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("available_stalls")
    suspend fun getAvailableStalls(): Response<List<Stall>>

//    @POST("register")
//    suspend fun register(): Response<User>
}
