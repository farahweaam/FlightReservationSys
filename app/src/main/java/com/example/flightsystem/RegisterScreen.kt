package com.example.flightsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    val context = LocalContext.current
    val db = remember { FlightDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val name = username.trim()
                when {
                    name.isEmpty() || password.isEmpty() -> {
                        isSuccess = false; message = "Please fill all fields"
                    }
                    name.length < 3 -> {
                        isSuccess = false; message = "Username must be at least 3 characters"
                    }
                    password.length < 6 -> {
                        isSuccess = false; message = "Password must be at least 6 characters"
                    }
                    password != confirm -> {
                        isSuccess = false; message = "Passwords don't match"
                    }
                    else -> scope.launch {
                        isLoading = true
                        val taken = withContext(Dispatchers.IO) {
                            db.userDao().getUserByUsername(name) != null ||
                                    db.adminDao().getAdminByUsername(name) != null
                        }
                        if (taken) {
                            isSuccess = false
                            message = "Username already taken"
                        } else {
                            withContext(Dispatchers.IO) {
                                db.userDao().insertUser(
                                    User(username = name, hashedpass = hashPassword(password))
                                )
                            }
                            isSuccess = true
                            message = "Account created! You can log in now."
                            username = ""; password = ""; confirm = ""
                        }
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Please wait..." else "Register")
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                color = if (isSuccess) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login")
        }
    }
}