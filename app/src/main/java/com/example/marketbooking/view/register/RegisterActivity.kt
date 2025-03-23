package com.example.marketbooking.view.register

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


            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color.LightGray)
                            .padding(16.dp)
                    ) {
                        Text("เมนู", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("หน้าแรก", modifier = Modifier.clickable { /* ไปหน้าแรก */ })
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("การตั้งค่า", modifier = Modifier.clickable { /* ไปหน้าตั้งค่า */ })
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ออกจากระบบ", modifier = Modifier.clickable { /* ออกจากระบบ */ })
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Green,
                                titleContentColor = Color.White
                            ),
                            title = {
                                Text("ลงทะเบียนเข้าใช้งาน")
                            }
                        )
                    },
                    content = { paddingValues ->
                        Box (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
//                        contentAlignment = Alignment.Center
                        ) {

                            if (showDialog.value ) {
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


                                OutlinedTextField(
                                    value = email.value,  // Changed: access value property
                                    onValueChange = { email.value = it },  // Changed: set value property
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = password.value,
                                    onValueChange = { password.value = it },
                                    label = { Text("Password") },
                                    modifier = Modifier.fillMaxWidth(),
//                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                                trailingIcon = {
//                                    val image = if (passwordVisible)
//                                        Icons.Filled.Visibility
//                                    else Icons.Filled.VisibilityOff
//
//                                    IconButton(onClick = {
//                                        passwordVisible = !passwordVisible
//                                    }) {
//                                        Icon(imageVector = image, "")
//                                    }
//                                }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = shopName.value,
                                    onValueChange = { shopName.value = it },
                                    label = { Text("ชื่อร้าน") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = firstName.value,
                                    onValueChange = { firstName.value = it },
                                    label = { Text("ชื่อ-สกุล") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = tel.value,
                                    onValueChange = { tel.value = it },
                                    label = { Text("หมายเลขโทรศัพท์") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = selectedCategory.value.categoryName,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("ประเภทลูกค้า") },
                                    trailingIcon = {
                                        IconButton(onClick = { expanded.value = true }) {
                                            Icon(Icons.Default.ArrowDropDown, "dropdown arrow")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
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
                                        // Log all the information
                                        println("Email: ${email.value}")
                                        println("Password: ${password.value}")
                                        println("Shop Name: ${shopName.value}")
                                        println("First Name: ${firstName.value}")
                                        println("Selected Category Id: ${selectedCategory.value.id}")

                                        scope.launch {
                                            register()
                                        }                                },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonColors(containerColor = Color.Green, contentColor = Color.White , disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray) // เปลี่ยนเป็นสีเขียว
                                ) {
                                    Text("ลงทะเบียน")
                                }
                            }
                        }

                    }
                )
            }



        }

    }

    private suspend fun register() {
        val user = User(
            userId =  null,
            email = email.value,
            password = password.value,
            shopName = shopName.value,
            name = firstName.value,
            bookingCategoryId = selectedCategory.value.id,
            tel = tel.value
        )

        try {
            val response = RetrofitClient.apiService.register(user)
            // ถ้าผ่าน ให้แสดง Dialog สำเร็จ
            showDialog.value = true
        } catch (e: Exception) {
            Log.e("API_RESPONSE", "Exception: ${e.message}")
        }
    }

    @Composable
    fun SuccessDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("สมัครสมาชิกสำเร็จ") },
            text = {
                Column {
                    Text("สำเร็จ")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("ตกลง")
                }
            }
        )
    }
}