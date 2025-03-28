package com.example.marketbooking.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.marketbooking.R
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.RequestBooking
import com.example.marketbooking.model.Stall
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.view.history.HistoryActivity
import com.example.marketbooking.view.holiday.HolidayActivity
import com.example.marketbooking.view.register.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DailyBookingActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var selectedStall: MutableState<Stall?>
    private lateinit var showLogoutDialog: MutableState<Boolean>
    private lateinit var showSuccessDialog: MutableState<Boolean>
    private lateinit var showFailDialog: MutableState<Boolean>
    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    private  var shopName = ""
    private lateinit var userId: String
    private lateinit var scope: CoroutineScope
    private lateinit var term: MutableState<String>

    val DarkForest = Color(0xFF102F15)
    val Sage = Color(0xFF728C5A)
    val PaleLime = Color(0xFFEAF1B1)
    val PastelMint = Color(0xFFEBFADC)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            scope = rememberCoroutineScope()
            val context = LocalContext.current // Add context
            isLoading = remember { mutableStateOf(true) }
            availableStalls = remember { mutableStateOf(emptyList()) }
            val scope = rememberCoroutineScope()
            showDialog = remember { mutableStateOf(false) }
            selectedStall = remember { mutableStateOf(null) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            showLogoutDialog = remember { mutableStateOf(false) }
            showFailDialog = remember { mutableStateOf(false) }
            userPreferences = UserPreferences(this)
            term =  remember { mutableStateOf("") }
            val user  = userPreferences.getUser()
            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ
            if (user != null) {
                userName = user.name
                shopName = user.shopName
                userId = user.userId.toString()
            } else {
                userName = "Guest" // Fallback value
                shopName = "Unknown Shop" // Fallback value
                userId = "0" // Fallback value
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

            if (showSuccessDialog.value) {
                BookingSuccessDialog(onDismiss = { showSuccessDialog.value = false })
            }

            if (showFailDialog.value) {
                BookingFailDialog(onDismiss = { showFailDialog.value = false })
            }

            // ใช้ LaunchedEffect เพื่อเรียกใช้ getAvailableStalls() ภายใน Coroutine
            LaunchedEffect(Unit) {
                fetchData()
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight() // ให้เต็มความสูง
                            .width(300.dp) // ปรับขนาดความกว้างของ Drawer
                            .background(DarkForest)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column {
                                Text("สวัสดี ${userName}", style = MaterialTheme.typography.headlineSmall.copy(color = PaleLime, fontSize = 25.sp , fontWeight = FontWeight.Bold))

                                Text("ร้าน ${shopName}", style = MaterialTheme.typography.headlineSmall.copy(color = PaleLime, fontSize = 25.sp , fontWeight = FontWeight.Bold))

                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, PaleLime, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว                                .clickable { /* ไปหน้าแรก */ }
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
                                    tint = PaleLime,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เว้นระยะระหว่างไอคอนกับข้อความ
                                Text(
                                    "ขอจองพื้นที่",
                                    color = PaleLime,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, PaleLime, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
                                .clickable {
                                    startActivity(Intent(context, HistoryActivity::class.java))
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
                                    tint = PaleLime, // ไอคอนเป็นสีขาว
                                    modifier = Modifier.size(24.dp) // กำหนดขนาดไอคอน
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เพิ่มระยะห่างระหว่างไอคอนกับข้อความ
                                Text(
                                    "ประวัติการจอง",
                                    color = PaleLime,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, PaleLime, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
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
                                    color = PaleLime,
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
                                containerColor = DarkForest,
                                titleContentColor = Color.White
                            ),
                            title = {
                                Text("จองพื้นที่ตลาด (รายวัน)" , style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontSize = 25.sp , fontWeight = FontWeight.Bold))
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            fetchData()
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
                                    .padding(paddingValues)
                                    .verticalScroll(rememberScrollState()), // เพิ่มการเลื่อนแนวตั้ง
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth() // ป้องกันการบีบตัว
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp,horizontal = 16.dp)
                                            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(PastelMint),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        ) {
                                            Spacer(modifier = Modifier.height(15.dp))
                                            Row {
                                                Icon(
                                                    imageVector = Icons.Filled.Home,
                                                    contentDescription = "Market Icon",
                                                    modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Text(
                                                    text = "กฏของตลาด",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold // ทำให้ตัวหนา
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(term.value)
                                            Spacer(modifier = Modifier.height(15.dp))

                                        }
                                    }
                                    MarketGrid() // องค์ประกอบข้างล่างที่ทำให้ scroll ได้
                                }
                            }

                        }
                    }
                )

            }
        }
    }
    private suspend fun fetchData() {
        try {
            isLoading.value = true
            getAvailableStalls()
            getTerm()
            isLoading.value = false
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun getAvailableStalls() {
        try {
            val response = RetrofitClient.apiService.getAvailableStallsDaily()
            availableStalls.value = response.body() ?: emptyList()
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun getTerm() {
        try {
            val response = RetrofitClient.apiService.getTerm()

            term.value = response.body()?.term ?: ""

        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private suspend fun bookingStall(requestBooking: RequestBooking) {
        try {
            val response = RetrofitClient.apiService.booking(requestBooking)
            // ถ้าจองสำเร็จ
            if (response.isSuccessful) {
                println("BookingSuccess")
                // แสดง Dialog Success
                showSuccessDialog.value = true
            }else{
//                responseMessage = response.message()
                showFailDialog.value = true
            }
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 16.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(PastelMint),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 10.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Blue.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ของใช้",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Green.copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ของกิน",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in 0 until 12) {
                            val seatLabel = "A${row + 1}"
                            val stall = availableStalls.value.find {
                                it.lineName == "A" && it.lineSequence == row + 1
                            }
                            val color = when {
                                stall == null -> Color.Gray
                                stall.bookingStatus == "ไม่ว่าง" -> Color.Red
                                else -> Color(0xFF006400)
                            }
                            MarketStall(text = seatLabel, color = color, stall = stall)
                        }
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in 0 until 12) {
                            val seatLabel = "B${row + 1}"
                            val stall = availableStalls.value.find {
                                it.lineName == "B" && it.lineSequence == row + 1
                            }
                            val color = when {
                                stall == null -> Color.Gray
                                stall.bookingStatus == "ไม่ว่าง" -> Color.Red
                                else -> Color(0xFF006400)
                            }
                            MarketStall(text = seatLabel, color = color, stall = stall)
                        }
                    }
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
//                    Text("จำนวนวันที่ว่าง: ${stall.availableDays}")
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
                    onClick = {
                        // สร้าง Request Object ของการจอง
                        var requestObject = RequestBooking(
                            stallId = stall.stallId,
                            bookingUserId = userId.toIntOrNull() ?: 0, // แปลง userId เป็น Int, ถ้าไม่ได้ให้เป็น 0
                            bookingCategoryId = 2,
                            price = stall.price.toIntOrNull() ?: 0 // แปลง price เป็น Int, ถ้าไม่ได้ให้เป็น 0
                        )
                        // ซ่อน Dialog จอง
                        showDialog.value = false
                        scope.launch {
                            bookingStall(requestObject)
                        }



                    },
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
    fun BookingSuccessDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = {
                // ไปหน้า Activity ประวัติ
            },
            title = {
                Text(
                    text = "จองสำเร็จ!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32) // สีเขียวเข้ม
                )
            },
            text = {
                Column {
                    Text("คุณได้ทำการจองแผงเรียบร้อยแล้ว", fontSize = 16.sp)
                    Text("ระบบจะพาท่านเข้าสู่ประวัติการจอง", fontSize = 16.sp)
                }
            },
            confirmButton = {
                Button(
                    // กดเพื่อเข้าสู่หน้าขำระเงิน
                    onClick = {
                        onDismiss()
                        startActivity(Intent(this, HistoryActivity::class.java))
                    },
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
    fun BookingFailDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = { /* ปิด dialog เมื่อคลิกด้านนอก */ },
            title = {
                Text(
                    text = "เกิดข้อผิดพลาด !!!",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red // สีแดง
                )
            },
            text = {
                Column {
                    Text("คุณเคยจองแล้วในเดือนนี้", fontSize = 16.sp)
                }
            },
            confirmButton = {
                Button(
                    // กดเพื่อปิด dialog
                    onClick = {
                        onDismiss()  // ปิด dialog
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red) // สีแดง
                ) {
                    Text("ตกลง", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    @Composable
    fun LogoutConfirmationDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = PastelMint, // พื้นหลัง Dialog สี PastelMint
            title = {
                Text(
                    "ยืนยันการออกจากระบบ",
                    color = DarkForest,
                    fontWeight = FontWeight.Bold // ทำให้ตัวหนา
                )
            },
            text = {
                Text(
                    "คุณต้องการออกจากระบบใช่หรือไม่?",
                    color = DarkForest,
                    fontWeight = FontWeight.Bold // ทำให้ตัวหนา
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = DarkForest, // ปุ่มตกลงเป็นสี DarkForest
                        contentColor = Color.White
                    )
                ) {
                    Text("ตกลง", fontWeight = FontWeight.Bold) // ทำให้ตัวหนังสือปุ่มตกลงหนา
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Sage // ปุ่มยกเลิกใช้ตัวอักษรสี Sage
                    )
                ) {
                    Text("ยกเลิก", fontWeight = FontWeight.Bold) // ทำให้ตัวหนังสือปุ่มยกเลิกหนา
                }
            }
        )
    }

}
