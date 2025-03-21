package com.example.marketbooking.view.register

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
class RegisterActivity : AppCompatActivity() {
    private lateinit var email: MutableState<String>
    private lateinit var password: MutableState<String>
    private lateinit var shopName: MutableState<String>
    private lateinit var firstName: MutableState<String>
    private lateinit var lastName: MutableState<String>
    private lateinit var passwordVisible: MutableState<Boolean>
    private val categories = listOf("ประจำ", "ขาจร")
    lateinit var expanded: MutableState<Boolean>
    private lateinit var selectedCategory: MutableState<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            email = remember { mutableStateOf("") }
            password = remember { mutableStateOf("") }
            shopName = remember { mutableStateOf("") }
            firstName = remember { mutableStateOf("") }
            lastName = remember { mutableStateOf("") }
            passwordVisible = remember { mutableStateOf(false) }
            expanded = remember { mutableStateOf(false) }
            selectedCategory = remember { mutableStateOf(categories[0]) }

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Blue,
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("ลงทะเบียนเข้าใช้งาน")
                        }
                    )
                }, content = { paddingValues ->
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
//                        contentAlignment = Alignment.Center
                    ) {
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
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = selectedCategory.value,
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
                                        text = { Text(category) },
                                        onClick = {
                                            selectedCategory.value = category
                                            expanded.value = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* Handle registration logic */ },
                                modifier = Modifier.fillMaxWidth()
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