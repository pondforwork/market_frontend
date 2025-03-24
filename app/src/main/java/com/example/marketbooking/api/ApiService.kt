package com.example.marketbooking.api

import com.example.marketbooking.model.RequestBooking
import com.example.marketbooking.model.Stall
import com.example.marketbooking.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("available_stalls")
    suspend fun getAvailableStalls(): Response<List<Stall>>

    @POST("register")
    suspend fun register(@Body user: User): Response<User>


    data class LoginRequest(val email: String, val password: String)
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("request_booking")
    suspend fun booking(@Body requestObject: RequestBooking): Response<Unit>
}
