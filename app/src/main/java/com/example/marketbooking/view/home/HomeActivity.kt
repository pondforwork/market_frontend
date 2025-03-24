package com.example.marketbooking.view.home

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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.draw.clip
import com.example.marketbooking.api.ApiService
import com.example.marketbooking.model.RequestBooking
import com.example.marketbooking.view.RegularBookingActivity
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
class HomeActivity : ComponentActivity() {
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
            userPreferences = UserPreferences(this)
            val user  = userPreferences.getUser()
            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ

            if(user!=null){
                userName = user.name
                userId = user.userId.toString()
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // พื้นหลังโปร่งใส
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว
                                .clickable {
                                    // เปิดหน้า Home
//                                    startActivity(Intent(context, HomeActivity::class.java))

                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // จัดไอคอนและข้อความให้อยู่ตรงกลางแนวตั้ง
                                horizontalArrangement = Arrangement.Center, // จัดให้อยู่ตรงกลางแนวนอน
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Home, // ใช้ไอคอน "ประวัติ"
                                    contentDescription = "ประวัติการจอง",
                                    tint = Color.White, // ไอคอนเป็นสีขาว
                                    modifier = Modifier.size(24.dp) // กำหนดขนาดไอคอน
                                )
                                Spacer(modifier = Modifier.width(8.dp)) // เพิ่มระยะห่างระหว่างไอคอนกับข้อความ
                                Text(
                                    "หน้าหลัก",
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
                                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)) // กรอบสีขาว                                .clickable { /* ไปหน้าแรก */ }
                                .padding(16.dp).clickable {
                                    // เปิดหน้า Home
                                    startActivity(Intent(context, RegularBookingActivity::class.java))
                                }
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

                        )
                    },
                    content = { paddingValues ->  // รับ parameter paddingValues
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues) // ใช้ paddingValues ที่มาจาก Scaffold
                                .padding(16.dp)  // สามารถใช้ padding เพิ่มเติมได้
                        ) {
                            Text(
                                text = "ยินดีต้อนรับสู่ระบบจองพื้นที่สำหรับรายเดือน",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "กรุณาเลือกแผงที่ท่านต้องการจอง",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                            )
                        }
                    }
                )

            }


        }
    }


}
