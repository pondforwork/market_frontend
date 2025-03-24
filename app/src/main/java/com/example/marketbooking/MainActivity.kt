package com.example.marketbooking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.view.RegularBookingActivity
import com.example.marketbooking.view.register.LoginActivity
import com.example.marketbooking.view.register.RegisterActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        val userFromStorage = userPreferences.getUser()

        if(userFromStorage != null){
            // ถ้ามี User อยู่และเป็นเข้าประจำ
            if(userFromStorage.bookingCategoryId == 1){
                startActivity(Intent(this, RegularBookingActivity::class.java))
            }else{
                // ถ้าเป็นขาจร
                startActivity(Intent(this, RegularBookingActivity::class.java))
            }
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }
//        setContent {
//            MaterialTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    MainScreen()
//                }
//            }
//        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hello World!")
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }
            ) {
                Text("ลงทะเบียนเข้าใช้งาน")
            }


//            Button(
//                onClick = {
//                    scope.launch {
//                        try {
//                            val response = RetrofitClient.apiService.getAvailableStalls()
//                            if (response.isSuccessful) {
//                                Log.d("API_RESPONSE", "Success: ${response.body()}")
//                            } else {
//                                Log.e("API_RESPONSE", "Error: ${response.errorBody()?.string()}")
//                            }
//                        } catch (e: Exception) {
//                            Log.e("API_RESPONSE", "Exception: ${e.message}")
//                        }
//                    }
//                }
//            ) {
//                Text("Call API")
//            }
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Button
            Button(
                onClick = {
                    context.startActivity(Intent(context, RegularBookingActivity::class.java))
                }
            ) {
                Text("Go to Regular Booking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Button
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            ) {
                Text("Login Page")
            }
        }
    }
}

@Composable
fun DefaultPreview() {
    MaterialTheme {
        MainScreen()
    }
}