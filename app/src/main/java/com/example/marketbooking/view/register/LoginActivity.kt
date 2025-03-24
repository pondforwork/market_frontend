package com.example.marketbooking.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.marketbooking.R
import com.example.marketbooking.api.ApiService
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.User
import com.example.marketbooking.utils.UserPreferences
import com.example.marketbooking.view.RegularBookingActivity
import kotlinx.coroutines.launch
import retrofit2.http.Body

class LoginActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        setContent {
            LoginScreen()
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            val request = ApiService.LoginRequest(email, password)
            val response = RetrofitClient.apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    userPreferences.saveUser(user)
                    // Login สำเร็จ เข้าสู่หน้าจอง
                    val userResponse =  userPreferences.getUser()

                    if(userResponse!=null){
                        // ถ้าเป็น ประจำ
                        if (userResponse.bookingCategoryId == 1) {
                            startActivity(Intent(this, RegularBookingActivity::class.java))
                        }else{
                            // ขาจร
                            startActivity(Intent(this, RegularBookingActivity::class.java))
                        }
                    }


                }
                } else {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            trailingIcon = {
//                val image = if (passwordVisible)
//                    Icons.Filled.Visibility
//                else Icons.Filled.VisibilityOff
//
//                IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                    Icon(imageVector = image, contentDescription = null)
//                }
//            }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        login(email,password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }


    }

}
