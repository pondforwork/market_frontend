package com.example.marketbooking.view.history

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.Stall
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.api.ApiService
import com.example.marketbooking.model.BookingHistory
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
class HistoryActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var selectedStall: MutableState<Stall?>
    private lateinit var showLogoutDialog: MutableState<Boolean>
    private lateinit var showSuccessDialog: MutableState<Boolean>
    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var scope: CoroutineScope
    private lateinit var historys: List<BookingHistory>;
    val DarkForest = Color(0xFF102F15)
    val Sage = Color(0xFF728C5A)
    val PaleLime = Color(0xFFEAF1B1)
    val PastelMint = Color(0xFFEBFADC)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LaunchedEffect(Unit) {
                getHistory()
            }

            scope = rememberCoroutineScope()
            val context = LocalContext.current // Add context
            isLoading = remember { mutableStateOf(false) }
            availableStalls = remember { mutableStateOf(emptyList()) }
            val scope = rememberCoroutineScope()
            showDialog = remember { mutableStateOf(false) }
            selectedStall = remember { mutableStateOf(null) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            showLogoutDialog = remember { mutableStateOf(false) }
            userPreferences = UserPreferences(this)
            val user  = userPreferences.getUser()
            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ
            historys = mutableListOf()
            if(user!=null){
                userName = user.name
                userId = user.userId.toString()
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkForest,
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("ประวัติการจองพื้นที่", style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold))
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
                            if (historys.isEmpty()) {
                                // Show message when no booking history
                                Text(
                                    text = "ยังไม่ได้ทำการจอง",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(historys) { history ->
                                        HistoryCard(history)
                                    }
                                }
                            }
                        }
                    }
                }
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun HistoryCard(history: BookingHistory) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                    val intent = Intent(this, HistoryDetailActivity::class.java).apply {
                        putExtra("booking_id", history.id.toString())
                    }
                    startActivity(intent)
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = PastelMint // ใช้สี PastelMint สำหรับพื้นหลังของ Card
            )        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "ชื่อแผง : ${history.stallName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkForest // ใช้สี DarkForest สำหรับข้อความชื่อแผง
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "สถานะ: ${getThaiStatus(history.status)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = getStatusColor(history.status) // ใช้สีที่เหมาะสมจากฟังก์ชัน getStatusColor()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "วันที่จอง: ${formatDate(history.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Sage // ใช้สี Sage สำหรับข้อความวันที่จอง
                )
            }
        }
    }

    // Add these helper functions
fun getThaiStatus(status: String): String {
    return when (status.lowercase()) {
        "pending" -> "รอการชำระเงิน"
        "purchased" -> "รอการอนุมัติ"
        "accepted" -> "อนุมัติแล้ว"
        "rejected" -> "ปฏิเสธ"
        "cancelled" -> "ยกเลิกแล้ว"
        else -> status
    }
}

fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pending" -> Color(0xFFFFA000) // Orange
        "approved" -> Color(0xFF4CAF50) // Green
        "rejected" -> Color(0xFFF44336) // Red
        else -> Color.Gray
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

    private suspend fun getHistory() {
        try {
            isLoading.value = true
            val requestObj = ApiService.HistoryRequest(userId)
            val response = RetrofitClient.apiService.getHistory(requestObj)

            if (response.isSuccessful) {
                val historyList = response.body()
                if (historyList != null && historyList.isNotEmpty()) {
                    historys = historyList
                } else {
                    Log.e("API_RESPONSE", "No booking history found.")
                }
            } else {
                Log.e("API_RESPONSE", "Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        } finally {
            isLoading.value = false
        }
    }




}
