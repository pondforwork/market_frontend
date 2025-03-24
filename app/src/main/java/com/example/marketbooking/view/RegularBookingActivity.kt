package com.example.marketbooking.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import com.example.marketbooking.MainActivity
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.Stall
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.view.register.LoginActivity
import com.example.marketbooking.view.register.RegisterActivity

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
class RegularBookingActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var selectedStall: MutableState<Stall?>
    private lateinit var showLogoutDialog: MutableState<Boolean>
    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
            if(user!=null){
                userName = user.name
            }


            if (showLogoutDialog.value) {
                LogoutConfirmationDialog(
                    onConfirm = {
                        userPreferences.clearUser()
                        startActivity(Intent(this, LoginActivity::class.java))
                    },
                    onDismiss = {
                        showLogoutDialog.value = false
                    }
                )
            }

            // ใช้ LaunchedEffect เพื่อเรียกใช้ getAvailableStalls() ภายใน Coroutine หรือ await async
            LaunchedEffect(Unit) {
                getAvailableStalls()
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight() // ให้เต็มความสูง
                            .width(300.dp) // ปรับขนาดความกว้างของ Drawer
                            .background(Color(0xFFFFA725))
//                            .background(Color(0xFFFFF5E4)) // เปลี่ยนเป็นโค้ดสีที่คุณต้องการ
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                                    contentAlignment = Alignment.TopStart
                        ) {
                            Text("สวัสดี ${userName}", style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp , fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว                                .clickable { /* ไปหน้าแรก */ }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart, // ใช้ไอคอนตะกร้า
                                    contentDescription = "จองพื้นที่",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เว้นระยะระหว่างไอคอนกับข้อความ
                                Text(
                                    "ขอจองพื้นที่",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
                                .clickable {
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // จัดไอคอนและข้อความให้อยู่ตรงกลางแนวตั้ง
                                horizontalArrangement = Arrangement.Center, // จัดให้อยู่ตรงกลางแนวนอน
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History, // ใช้ไอคอน "ประวัติ"
                                    contentDescription = "ประวัติการจอง",
                                    tint = Color.White, // ไอคอนเป็นสีขาว
                                    modifier = Modifier.size(24.dp) // กำหนดขนาดไอคอน
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เพิ่มระยะห่างระหว่างไอคอนกับข้อความ
                                Text(
                                    "ประวัติการจอง",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
                                .clickable {
                                }
                                .padding(16.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically, // จัดไอคอนและข้อความให้อยู่ตรงกลางแนวตั้ง
                                horizontalArrangement = Arrangement.Center, // จัดให้อยู่ตรงกลางแนวนอน
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth, // ใช้ไอคอน "ประวัติ"
                                    contentDescription = "แจ้งวันหยุด",
                                    tint = Color.White, // ไอคอนเป็นสีขาว
                                    modifier = Modifier.size(24.dp) // กำหนดขนาดไอคอน
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เพิ่มระยะห่างระหว่างไอคอนกับข้อความ
                                Text(
                                    "แจ้งวันหยุด",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
                                .clickable { 
                                    showLogoutDialog.value = true 
                                 }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // จัดไอคอนและข้อความให้อยู่ตรงกลางแนวตั้ง
                                horizontalArrangement = Arrangement.Center, // จัดให้อยู่ตรงกลางแนวนอน
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Logout, // ใช้ไอคอน "ประวัติ"
                                    contentDescription = "ออกจากระบบ",
                                    tint = Color.Red, // ไอคอนเป็นสีขาว
                                    modifier = Modifier.size(24.dp) // กำหนดขนาดไอคอน
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "ออกจากระบบ",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFFFFA725),
                                titleContentColor = Color.White
                            ),
                            title = {
                                Text("จองพื้นที่ตลาด (รายเดือน)" , style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp , fontWeight = FontWeight.Bold))
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            getAvailableStalls()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = Color.White
                                    )
                                }
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
        if (showDialog.value && selectedStall.value != null) {
            StallDialog(
                stall = selectedStall.value!!,
                onDismiss = { showDialog.value = false }
            )
        }

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
                    MarketStall(text = seatLabel, color = color, stall = stall)
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
                    MarketStall(text = seatLabel, color = color, stall = stall)
                }
            }
        }
    }

    @Composable
    fun MarketStall(text: String, color: Color, stall: Stall?) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
                .clickable {
                    if (stall != null) {
                        selectedStall.value = stall
                        showDialog.value = true
                    }
                },
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

    @Composable
    fun StallDialog(stall: Stall, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "รายละเอียดแผง ${stall.stallName}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("สถานะ: ${stall.bookingStatus}")
                    Text("จำนวนวันที่ว่าง: ${stall.availableDays}")
                    Text("ราคาที่ต้องชำระ: ${stall.price}")

//              Text("จำนวนวันที่ว่าง: ${stall.availableDays}/${stall.totalDays}")

                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text("ยกเลิก", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFFFA725)) // สีส้ม
                ) {
                    Text("จอง", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }


    @Composable
    fun LogoutConfirmationDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("ยืนยันการออกจากระบบ") },
            text = { Text("คุณต้องการออกจากระบบใช่หรือไม่?") },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}
