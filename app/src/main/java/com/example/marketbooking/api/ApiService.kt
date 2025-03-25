package com.example.marketbooking.api

import com.example.marketbooking.model.BookingHistory
import com.example.marketbooking.model.CanHoliday
import com.example.marketbooking.model.RequestBooking
import com.example.marketbooking.model.Stall
import com.example.marketbooking.model.Term
import com.example.marketbooking.model.User
import com.example.marketbooking.model.isHolidaySuccess
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("term")
    suspend fun getTerm(): Response<Term>

    @GET("available_stalls")
    suspend fun getAvailableStalls(): Response<List<Stall>>

    @POST("register")
    suspend fun register(@Body user: User): Response<User>

    data class LoginRequest(val email: String, val password: String)
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("request_booking")
    suspend fun booking(@Body requestObject: RequestBooking): Response<Unit>

    data class HistoryRequest(val booking_user_id: String)
    @POST("history")
    suspend fun getHistory(@Body request: HistoryRequest): Response<List<BookingHistory>>

    @GET("canholiday")
    suspend fun canHoliday(@Query("booking_user_id") bookingUserId: String): Response<CanHoliday>


    data class MakeHolidayRequest(val booking_user_id: String)
    @POST("makeholiday")
    suspend fun makeHoliday(@Body request: MakeHolidayRequest): Response<isHolidaySuccess>
}
