package com.example.marketbooking.view.history

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.marketbooking.model.Stall
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.model.BookingHistory
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.marketbooking.api.ApiService
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.BookingDetail
import com.example.marketbooking.view.DailyBookingActivity
import com.example.marketbooking.view.RegularBookingActivity
import com.example.marketbooking.view.register.RegisterActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class HistoryDetailActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var showSuccessDialog: MutableState<Boolean>
    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var scope: CoroutineScope
    private lateinit var historys: List<BookingHistory>
    private lateinit var showConfirmDialog: MutableState<Boolean>
    private lateinit var showCancelDialog: MutableState<Boolean>
    private lateinit var bookingId: MutableState<Int>
    private lateinit var bookingType: MutableState<Int>
    private lateinit var bookingDetail: MutableState<BookingDetail>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            bookingId = remember { mutableIntStateOf(-1) }

            bookingId.value = intent.getStringExtra("booking_id")?.toIntOrNull() ?: -1
            bookingDetail = remember { mutableStateOf(BookingDetail(stallName = "", status = "", createdAt = "", price = "")) }
            // Get Detail ที่นี่
            LaunchedEffect(Unit) {
                fetchData()
            }

            scope = rememberCoroutineScope()
            val context = LocalContext.current // Add context
            isLoading = remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            showDialog = remember { mutableStateOf(false) }
            userPreferences = UserPreferences(this)
            val user  = userPreferences.getUser()
            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ
            historys = mutableListOf()
            showConfirmDialog = remember { mutableStateOf(false) }
            showCancelDialog = remember { mutableStateOf(false) }
            bookingType = remember { mutableStateOf(-1) }

            if(user!=null){
                userName = user.name
                userId = user.userId.toString()
                bookingType.value = user.bookingCategoryId
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFFFFA725),
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("รายละเอียดการจองพื้นที่", style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold))
                        },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        if (isLoading.value) {
                            // Show loading indicator
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            // Display receipt details
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(Color.White)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                    .padding(16.dp)
                            ) {
//                                Text(
//                                    text = "Id: ${bookingId.value}",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    fontWeight = FontWeight.Bold
//                                )
                                Text(
                                    text = "ชื่อแผง: ${bookingDetail.value.stallName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "วันที่จอง: ${bookingDetail.value.createdAt}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ราคา: ${bookingDetail.value.price}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "สถานะการชำระเงิน: ${bookingDetail.value.status.let { getThaiStatus(it) }}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (bookingDetail.value.status == "pending") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = { showCancelDialog.value = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "ยกเลิก",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Button(
                                            onClick = { showConfirmDialog.value = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Green,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "ยืนยันการชำระเงิน",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )

            if (showConfirmDialog.value) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog.value = false },
                    title = { 
                        Text(
                            text = "ยืนยันการชำระเงิน", 
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        ) 
                    },
                    text = { 
                        Text(
                            text = "คุณแน่ใจหรือไม่ว่าต้องการยืนยันการชำระเงิน?", 
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        ) 
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { 
                                showConfirmDialog.value = false
                                scope.launch {
                                    submitPurchase()
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ยืนยัน", 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showConfirmDialog.value = false },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ยกเลิก", 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }

            if (showCancelDialog.value) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog.value = false },
                    title = { 
                        Text(
                            text = "ยกเลิกการจอง", 
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        ) 
                    },
                    text = { 
                        Text(
                            text = "คุณแน่ใจหรือไม่ว่าต้องการยกเลิกการจอง?", 
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        ) 
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { 
                                showCancelDialog.value = false
                                scope.launch {
                                    cancelBooking()
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ยืนยัน", 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showCancelDialog.value = false },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ยกเลิก", 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        return try {
            val dateTime = LocalDateTime.parse(dateString, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (e: Exception) {
            dateString
        }
    }

    fun getThaiStatus(status: String): String {
        return when (status.lowercase()) {
            "pending" -> "รอการชำระเงิน"
            "purchased" -> "ชำระเงินแล้ว รอการอนุมัติ"
            "accepted" -> "อนุมัติแล้ว"
            "rejected" -> "ปฏิเสธ"
            "cancelled" -> "ยกเลิกแล้ว"
            else -> status
        }
    }

    private suspend fun fetchData() {
        try {
            isLoading.value = true
            getDetail()
            isLoading.value = false
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun getDetail() {
        try {
            val response = RetrofitClient.apiService.getBookingDetail(bookingRequestId = bookingId.value.toString())

            if (response.body() != null) {
                val responseBody = response.body()!!

                // ตรวจสอบว่า responseBody เป็น List หรือไม่
                if (responseBody.isNotEmpty()) {
                    bookingDetail.value = responseBody[0] as BookingDetail // ต้อง cast ให้เป็น object ที่ต้องการ
                } else {
                    Log.e("API_RESPONSE", "Response body is not a list or is empty")
                }
            } else {
                Log.e("API_RESPONSE", "Response body is null")
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun submitPurchase() {
        try {
            val response = RetrofitClient.apiService.cancelBooking(ApiService.SubmitPurchase(bookingId.value.toString()))
            println(response.toString())
            // ถ้ายืนยันสำเร็จ กลับหน้าหลัก
            if(response.isSuccessful){
                if(bookingType.value == 1){
                    startActivity(Intent(this, RegularBookingActivity::class.java))
                }else{
                    startActivity(Intent(this, DailyBookingActivity::class.java))
                }
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun cancelBooking() {
        try {

            val response = RetrofitClient.apiService.cancelBooking(ApiService.CancelRequest(bookingId.value.toString()))
            println(response.toString())
            // ถ้ายกเลิกสำเร็จย้อนกลับไปหน้าหลัก
            // ถ้าเป็นเจ้าประจำ
            if(response.isSuccessful){
                if(bookingType.value == 1){
                    startActivity(Intent(this, RegularBookingActivity::class.java))
                }else{
                    startActivity(Intent(this, DailyBookingActivity::class.java))
                }
            }

        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }
}
