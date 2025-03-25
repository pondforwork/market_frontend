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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.res.painterResource
import com.example.marketbooking.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
class HolidayActivity : ComponentActivity() {
    private lateinit var isLoading: MutableState<Boolean>
    private lateinit var availableStalls: MutableState<List<Stall>>
    private lateinit var showDialog: MutableState<Boolean>
    private lateinit var selectedStall: MutableState<Stall?>
    private lateinit var showLogoutDialog: MutableState<Boolean>
    private lateinit var showSuccessDialog: MutableState<Boolean>
    private lateinit var showConfirmDialog: MutableState<Boolean>

    private lateinit var userPreferences: UserPreferences
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var scope: CoroutineScope
    private lateinit var historys: List<BookingHistory>;
    private lateinit var canHoliday: MutableState<Boolean>

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
            canHoliday = remember { mutableStateOf(false) }
            showSuccessDialog = remember { mutableStateOf(false) } // สร้าง state คุม Dialog สำเร็จ
            showConfirmDialog = remember { mutableStateOf(false) }
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
                            if (canHoliday.value) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        onClick = { showConfirmDialog.value = true },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50), // Green color for holiday request
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .height(60.dp)
                                            .width(200.dp)
                                    ) {
                                        Text(
                                            "ลาหยุดวันนี้",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
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
                                            modifier = Modifier.size(150.dp)
                                        )


                                        Text(
                                            "อยู่นอกเวลาแจ้งวันหยุด",
                                            color = Color.Red,
                                            textAlign = TextAlign.Center,
                                            fontSize = 24.sp // Increased font size
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (showConfirmDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDialog.value = false },
                            title = { Text("ยืนยันการหยุด") },
                            text = { Text("คุณต้องการหยุดวันนี้หรือไม่?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showConfirmDialog.value = false
                                        // Handle holiday request
                                    }
                                ) {
                                    Text("ยืนยัน")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showConfirmDialog.value = false }
                                ) {
                                    Text("ยกเลิก")
                                }
                            }
                        )
                    }
                }
            )

        }
    }

    private suspend fun iscanHoliday() {
        try {
            isLoading.value = true
            val holidayResponse = RetrofitClient.apiService.canHoliday()
            canHoliday.value = holidayResponse.isSuccessful && holidayResponse.body()?.status == "available"
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
            historys = emptyList()
        } finally {
            isLoading.value = false
        }
    }
}
