package com.example.marketbooking.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.Stall

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
class RegularBookingActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            isLoading = remember { mutableStateOf(true) }
            availableStalls = remember { mutableStateOf(emptyList()) }
            val scope = rememberCoroutineScope()

            // ใช้ LaunchedEffect เพื่อเรียกใช้ getAvailableStalls() ภายใน Coroutine
            LaunchedEffect(Unit) {
                getAvailableStalls()
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Blue,
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("จองพื้นที่ตลาด (รายเดือน)")
                        },
                        actions = {
                            Text(
                                text = "รีเฟรช",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable {
                                        scope.launch {
                                            getAvailableStalls()
                                        }

                                    }
                            )
                        }
                    )
                }, content = { paddingValues ->
                    
                    if (isLoading.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("กำลังดึงข้อมูล โปรดรอ...")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center
                        ) {
                            MarketGrid()
                        }
                    }
                }
            )
        }
    }

    private suspend fun getAvailableStalls() {
        try {
            isLoading.value = true
            val response = RetrofitClient.apiService.getAvailableStalls()
            availableStalls.value = response.body() ?: emptyList()
            isLoading.value = false
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    @Composable
    fun MarketGrid() {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // แถว A (ซ้าย)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in 0 until 12) {
                    val seatLabel = "A${row + 1}"
                    val stall = availableStalls.value.find {
                        it.lineName == "A" && it.lineSequence == row + 1
                    }
                    val color = when {
                        stall == null -> Color.Gray
                        stall.bookingStatus == "มีการจองบางวัน" -> Color.Red
                        else -> Color(0xFF006400)
                    }
                    MarketStall(text = seatLabel, color = color)
                }
            }

            // แถว B (ขวา)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in 0 until 12) {
                    val seatLabel = "B${row + 1}"
                    val stall = availableStalls.value.find {
                        it.lineName == "B" && it.lineSequence == row + 1
                    }
                    val color = when {
                        stall == null -> Color.Gray
                        stall.bookingStatus == "มีการจองบางวัน" -> Color.Red
                        else -> Color(0xFF006400)
                    }
                    MarketStall(text = seatLabel, color = color)
                }
            }
        }
    }

    @Composable
    fun MarketStall(text: String, color: Color) {
        Box(
            modifier = Modifier
                .size(50.dp) // ขนาดของที่นั่ง (ปรับได้ตามต้องการ)
                .background(color, shape = RoundedCornerShape(4.dp))
                .clickable { /* เพิ่ม logic การเลือกที่นั่ง */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }


}
