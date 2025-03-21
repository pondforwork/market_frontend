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
import com.example.marketbooking.api.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

class RegularBookingActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            isLoading = remember { mutableStateOf(true) }
            availableStalls = remember { mutableStateOf(emptyList()) }
//            var availableStalls by remember { mutableStateOf<List<String>>(emptyList()) }

            // Simulate loading
//            LaunchedEffect(Unit) {
//                delay(2000) // Simulate a 2-second loading time
//                isLoading = false
//            }

            var availableStalls by remember { mutableStateOf<List<String>>(emptyList()) }

            // ใช้ LaunchedEffect เพื่อเรียกใช้ getAvailableStalls() ภายใน Coroutine
            LaunchedEffect(Unit) {
                getAvailableStalls()
            }


            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("จองพื้นที่ตลาด")
                    if (isLoading.value) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading...")
                        }
                    } else {
                        MarketGrid()
                    }
                }
            }


        }

    }

    private suspend fun getAvailableStalls() {
        try {
            isLoading.value = true
            val response = RetrofitClient.apiService.getAvailableStalls()
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
            horizontalArrangement = Arrangement.SpaceBetween // ให้แถว A ชิดซ้ายและแถว B ชิดขวา
        ) {
            // แถว A (ชิดซ้าย)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between seats
            ) {
                for (row in 0 until 12) {
                    val seatLabel = "A${row + 1}"
                    MarketStall(text = seatLabel, color = Color.Blue)
                }
            }

            // แถว B (ชิดขวา)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between seats
            ) {
                for (row in 0 until 12) {
                    val seatLabel = "B${row + 1}"
                    MarketStall(text = seatLabel, color = Color.Green)
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
