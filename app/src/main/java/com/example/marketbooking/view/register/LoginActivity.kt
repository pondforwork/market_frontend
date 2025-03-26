package com.example.marketbooking.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.marketbooking.api.ApiService
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.view.DailyBookingActivity
import com.example.marketbooking.view.RegularBookingActivity
import kotlinx.coroutines.launch
import android.util.Patterns

class LoginActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    val DarkForest = Color(0xFF102F15)
    val Sage = Color(0xFF728C5A)
    val PaleLime = Color(0xFFEAF1B1)
    val PastelMint = Color(0xFFEBFADC)
    val MainBackground = Color(0xFFF5F5F5) // สีพื้นหลังหลักใหม่
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        setContent {
            MaterialTheme {
                LoginScreen()
            }
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            val request = ApiService.LoginRequest(email, password)
            val response = RetrofitClient.apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userPreferences.saveUser(user)
                    val userResponse = userPreferences.getUser()
                    val toast =  Toast.makeText(
                        this@LoginActivity,
                        "เข้าสู่ระบบสำเร็จ",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 150)

                    toast.show()
                    if(userResponse != null) {
                        if (userResponse.bookingCategoryId == 1) {
                            finish()
                            startActivity(Intent(this, RegularBookingActivity::class.java))
                        } else {
                            finish()
                            startActivity(Intent(this, DailyBookingActivity::class.java))
                        }
                    }
                }
            } else {
                   val toast =  Toast.makeText(
                        this@LoginActivity,
                        "อีเมลหรือรหัสผ่านไม่ถูกต้อง",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 150)

                toast.show()
                Log.e("API_RESPONSE", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    @Composable
    fun LoginScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        fun validateInputs(): Boolean {
            return when {
                email.isBlank() -> {
                    Toast.makeText(this@LoginActivity, "กรุณากรอกอีเมล", Toast.LENGTH_SHORT).show()
                    false
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this@LoginActivity, "รูปแบบอีเมลไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
                    false
                }
                password.isBlank() -> {
                    Toast.makeText(this@LoginActivity, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> true
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainBackground) // ใช้สีพื้นหลังหลักใหม่
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleLime) // การ์ดสี PaleLime
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "เข้าสู่ระบบ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DarkForest, // เปลี่ยนสีข้อความให้เข้มขึ้น
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("อีเมล", color = DarkForest) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("รหัสผ่าน", color = DarkForest) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (validateInputs()) {
                                    scope.launch {
                                        login(email, password)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(DarkForest) // ปุ่มเข้าสู่ระบบสี DarkForest
                        ) {
                            Text("เข้าสู่ระบบ", color = Color.White) // ตัวอักษรสีขาว
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Handle registration button click
                                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(Sage) // ปุ่มสมัครสมาชิกสี Sage
                        ) {
                            Text("สมัครสมาชิก", color = Color.White) // ตัวอักษรสีขาว
                        }
                    }
                }
            }
        }
    }
}