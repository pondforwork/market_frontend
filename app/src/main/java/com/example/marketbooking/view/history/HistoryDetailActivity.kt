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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get Detail ที่นี่
//            LaunchedEffect(Unit) {
//
//            }
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
            if(user!=null){
                userName = user.name
                userId = user.userId.toString()
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
                                Text(
                                    text = "ชื่อแผง: ${historys.firstOrNull()?.stallName ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "วันที่จอง: ${historys.firstOrNull()?.createdAt?.let { formatDate(it) } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "สถานะการชำระเงิน: ${historys.firstOrNull()?.status?.let { getThaiStatus(it) } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
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
            )

            if (showConfirmDialog.value) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog.value = false },
                    title = { Text("ยืนยันการชำระเงิน") },
                    text = { Text("คุณแน่ใจหรือไม่ว่าต้องการยืนยันการชำระเงิน?") },
                    confirmButton = {
                        TextButton(onClick = { 
                            showConfirmDialog.value = false
                            // Handle confirm payment action
                        }) {
                            Text("ยืนยัน")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog.value = false }) {
                            Text("ยกเลิก")
                        }
                    }
                )
            }

            if (showCancelDialog.value) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog.value = false },
                    title = { Text("ยกเลิกการจอง") },
                    text = { Text("คุณแน่ใจหรือไม่ว่าต้องการยกเลิกการจอง?") },
                    confirmButton = {
                        TextButton(onClick = { 
                            showCancelDialog.value = false
                            // Handle cancel booking action
                        }) {
                            Text("ยืนยัน")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog.value = false }) {
                            Text("ยกเลิก")
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
            "purchased" -> "รอการอนุมัติ"
            "accepted" -> "อนุมัติแล้ว"
            "rejected" -> "ปฏิเสธ"
            else -> status
        }
    }
}
