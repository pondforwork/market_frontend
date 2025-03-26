package com.example.marketbooking.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.marketbooking.api.RetrofitClient
import com.example.marketbooking.model.BookingCategory
import com.example.marketbooking.model.Stall
import com.example.marketbooking.model.User
import kotlinx.coroutines.launch
val DarkForest = Color(0xFF102F15)
val Sage = Color(0xFF728C5A)
val PaleLime = Color(0xFFEAF1B1)
val PastelMint = Color(0xFFEBFADC)

@OptIn(ExperimentalMaterial3Api::class)
class RegisterActivity : AppCompatActivity() {
    private lateinit var email: MutableState<String>
    private lateinit var password: MutableState<String>
    private lateinit var shopName: MutableState<String>
    private lateinit var firstName: MutableState<String>
    private lateinit var lastName: MutableState<String>
    private lateinit var tel: MutableState<String>
    private lateinit var passwordVisible: MutableState<Boolean>
    private val categories = BookingCategory.values().toList()
    lateinit var expanded: MutableState<Boolean>
    private lateinit var selectedCategory: MutableState<BookingCategory>
    private lateinit var showDialog: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            email = remember { mutableStateOf("") }
            password = remember { mutableStateOf("") }
            shopName = remember { mutableStateOf("") }
            firstName = remember { mutableStateOf("") }
            lastName = remember { mutableStateOf("") }
            tel = remember { mutableStateOf("") }
            passwordVisible = remember { mutableStateOf(false) }
            expanded = remember { mutableStateOf(false) }
            selectedCategory = remember { mutableStateOf(categories[0]) }
            val scope = rememberCoroutineScope()
            showDialog = remember { mutableStateOf(false) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkForest, // ใช้สี DarkForest
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("ลงทะเบียนเข้าใช้งาน")
                        }
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PastelMint) // พื้นหลังสี PastelMint
                            .padding(paddingValues)
                    ) {
                        if (showDialog.value) {
                            SuccessDialog(
                                onDismiss = { showDialog.value = false }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Sage, // ใช้ Sage เป็นสีขอบเมื่อโฟกัส
                                unfocusedBorderColor = DarkForest, // ใช้ DarkForest เป็นสีขอบเมื่อไม่ได้โฟกัส
                                focusedTextColor = DarkForest,
                            )

                            OutlinedTextField(
                                value = email.value,
                                onValueChange = { email.value = it },
                                label = { Text("Email", color = DarkForest) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = password.value,
                                onValueChange = { password.value = it },
                                label = { Text("Password", color = DarkForest) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = shopName.value,
                                onValueChange = { shopName.value = it },
                                label = { Text("ชื่อร้าน", color = DarkForest) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = firstName.value,
                                onValueChange = { firstName.value = it },
                                label = { Text("ชื่อ-สกุล", color = DarkForest) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = tel.value,
                                onValueChange = { tel.value = it },
                                label = { Text("หมายเลขโทรศัพท์", color = DarkForest) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = selectedCategory.value.categoryName,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("ประเภทลูกค้า", color = DarkForest) },
                                trailingIcon = {
                                    IconButton(onClick = { expanded.value = true }) {
                                        Icon(Icons.Default.ArrowDropDown, "dropdown arrow")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )

                            DropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.categoryName) },
                                        onClick = {
                                            selectedCategory.value = category
                                            expanded.value = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (validateInputs()) {
                                        scope.launch {
                                            register()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkForest, // ใช้สี DarkForest สำหรับปุ่ม
                                    contentColor = Color.White
                                )
                            ) {
                                Text("ลงทะเบียน")
                            }
                        }
                    }
                }
            )
        }
    }

    private suspend fun register() {
        val user = User(
            userId = null,
            email = email.value,
            password = password.value,
            shopName = shopName.value,
            name = firstName.value,
            bookingCategoryId = selectedCategory.value.id,
            tel = tel.value
        )

        try {
            val response = RetrofitClient.apiService.register(user)
            showDialog.value = true
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    private fun validateInputs(): Boolean {
        return when {
            email.value.isBlank() -> {
                Log.e("VALIDATION", "Email is required")
                false
            }
            password.value.isBlank() -> {
                Log.e("VALIDATION", "Password is required")
                false
            }
            shopName.value.isBlank() -> {
                Log.e("VALIDATION", "Shop name is required")
                false
            }
            firstName.value.isBlank() -> {
                Log.e("VALIDATION", "First name is required")
                false
            }
            tel.value.isBlank() -> {
                Log.e("VALIDATION", "Telephone number is required")
                false
            }
            else -> true
        }
    }

    @Composable
    fun SuccessDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = PastelMint,
            titleContentColor = DarkForest,
            textContentColor = Sage,
            title = { 
                Text(
                    "สมัครสมาชิกสำเร็จ",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = { 
                Text(
                    "ระบบจะพาท่านเข้าสู่หน้าเข้าสู่ระบบ",
                    style = MaterialTheme.typography.bodyLarge
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = DarkForest
                    )
                ) {
                    Text(
                        "ตกลง",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        )
    }
}
