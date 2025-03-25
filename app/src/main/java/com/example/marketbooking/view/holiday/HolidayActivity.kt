package com.example.marketbooking.view.holiday
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.marketbooking.model.BookingHistory
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.painterResource
import com.example.marketbooking.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import com.example.marketbooking.api.ApiService
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class HolidayActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var selectedStall: MutableState<Stall?>
    private lateinit var showLogoutDialog: MutableState<Boolean>
    private lateinit var showSuccessDialog: MutableState<Boolean>
    private lateinit var showConfirmDialog: MutableState<Boolean>
    private lateinit var showFailureDialog: MutableState<Boolean>
    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var scope: CoroutineScope
    private lateinit var historys: List<BookingHistory>;
//    private lateinit var canHoliday: MutableState<Boolean>

    private lateinit var holidayStatus: MutableState<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LaunchedEffect(Unit) {
                iscanHoliday()
            }

            scope = rememberCoroutineScope()
            val context = LocalContext.current // Add context
            isLoading = remember { mutableStateOf(true) }
            availableStalls = remember { mutableStateOf(emptyList()) }
            val scope = rememberCoroutineScope()
            showDialog = remember { mutableStateOf(false) }
            selectedStall = remember { mutableStateOf(null) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            showLogoutDialog = remember { mutableStateOf(false) }
            userPreferences = UserPreferences(this)
            val user  = userPreferences.getUser()
//            canHoliday = remember { mutableStateOf(false) }

            holidayStatus = remember { mutableStateOf("") }

            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ
            showConfirmDialog = remember { mutableStateOf(false) }
            showFailureDialog = remember { mutableStateOf(false) }
            historys = mutableListOf()
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
                            Text("แจ้งวันหยุด", style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold))
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
                            if (holidayStatus.value == "available") {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = R.drawable.undraw_departing_010k),
                                            contentDescription = "Example Image",
                                            modifier = Modifier.size(230.dp)
                                        )

                                        Text(
                                            "สามารถแจ้งหยุดร้านได้",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            fontSize = 24.sp ,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { showConfirmDialog.value = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50), // Green color for holiday request
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier
                                                .padding(16.dp)
                                                .height(45.dp)
                                                .width(200.dp)
                                        ) {
                                            Text(
                                                "ลาหยุดวันนี้",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            } else if (holidayStatus.value == "unavailable") {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.undraw_access_denied_krem),
                                            contentDescription = "Example Image",
                                            modifier = Modifier.size(230.dp)
                                        )
                                        Text(
                                            "อยู่นอกเวลาแจ้งวันหยุด",
                                            color = Color.Red,
                                            textAlign = TextAlign.Center,
                                            fontSize = 24.sp // Increased font size
                                        )
                                    }
                                }
                            }else{
                                // หยุดไปแล้ว
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = R.drawable.undraw_departing_010k),
                                            contentDescription = "Example Image",
                                            modifier = Modifier.size(230.dp)
                                        )

                                        Text(
                                            "คุณได้ทำการแจ้งหยุดไปแล้ว",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            fontSize = 24.sp ,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (showConfirmDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDialog.value = false },
                            title = {
                                Text(
                                    "ยืนยันการหยุด",
                                    fontWeight = FontWeight.Bold // ทำให้ตัวหนา
                                )
                            },
                            text = {
                                Text(
                                    "คุณต้องการหยุดวันนี้หรือไม่?",
                                    fontWeight = FontWeight.Bold // ทำให้ตัวหนา
                                )
                            },
                            // ยืนยันการหยุด
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showConfirmDialog.value = false
                                        scope.launch {
                                            makeHoliday()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // ปรับปุ่มเป็นสีแดง
                                ) {
                                    Text("ยืนยัน", fontWeight = FontWeight.Bold, color = Color.White) // ตัวหนังสือหนา สีขาว
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = { showDialog.value = false }
                                ) {
                                    Text("ยกเลิก", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                    if (showSuccessDialog.value) {
                        HolidaySuccessDialog(onDismiss = { showSuccessDialog.value = false })
                    }
                    if (showFailureDialog.value) {
                        BookingFailureDialog(onDismiss = { showFailureDialog.value = false })
                    }
                }
            )

        }
    }

    private suspend fun iscanHoliday() {
        try {
            isLoading.value = true
            val holidayResponse = RetrofitClient.apiService.canHoliday(userId.toString())
//            canHoliday.value = holidayResponse.isSuccessful && holidayResponse.body()?.status == "available"

            if(holidayResponse.isSuccessful && holidayResponse.body()?.status!=null){
                holidayStatus.value = holidayResponse.body()?.status.toString()
                println(holidayStatus.value)
            }


        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
            historys = emptyList()
        } finally {
            isLoading.value = false
        }
    }

    private suspend fun makeHoliday() {
        try {
            val requestObj = ApiService.MakeHolidayRequest(userId.toString())
            val holidayResponse = RetrofitClient.apiService.makeHoliday(requestObj)
            if (holidayResponse.isSuccessful && holidayResponse.body()?.status == "success") {
                showSuccessDialog.value = true
            } else {
                showFailureDialog.value = true
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
            showFailureDialog.value = true
        }
    }
}

@Composable
fun HolidaySuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "แจ้งวันหยุดสำเร็จ",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32) // สีเขียวเข้ม
            )
        },
        text = {
            Column {
                Text("คุณได้ทำการแจ้งวันหยุดเรียบร้อยแล้ว", fontSize = 16.sp)
                Text("ระบบจะพาท่านกลับสู่หน้าหลัก", fontSize = 16.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF2E7D32)) // สีเขียว
            ) {
                Text("ตกลง", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun BookingFailureDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "แจ้งวันหยุดไม่สำเร็จ",
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        },
        text = {
            Column {
                Text("เกิดข้อผิดพลาดในการแจ้งวันหยุด", fontSize = 16.sp)
                Text("กรุณาลองใหม่อีกครั้ง", fontSize = 16.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("ตกลง", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
