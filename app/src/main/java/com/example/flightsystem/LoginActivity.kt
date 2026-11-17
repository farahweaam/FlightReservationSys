package com.example.flightsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.flightsystem.ui.theme.FlightSystemTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Already logged in? Skip straight to the right home screen.
        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        val savedRole = prefs.getString("role", null)
        if (savedRole != null) {
            openHome(savedRole, prefs.getInt("userId", -1))
            return
        }

        // Create the default admin on first launch
        val db = FlightDatabase.getInstance(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            if (db.adminDao().getAdminByUsername("admin") == null) {
                db.adminDao().insertAdmin(
                    Admin(username = "admin", hashedpass = hashPassword("admin123"))
                )
            }
        }

        setContent {
            FlightSystemTheme {
                var showRegister by remember { mutableStateOf(false) }

                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (showRegister) {
                            RegisterScreen(onBackToLogin = { showRegister = false })
                        } else {
                            LoginScreen(
                                onLoginSuccess = { role, id ->
                                    prefs.edit()
                                        .putString("role", role)
                                        .putInt("userId", id)
                                        .apply()
                                    openHome(role, id)
                                },
                                onRegisterClick = { showRegister = true }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openHome(role: String, id: Int) {
        val intent = if (role == "admin") {
            Intent(this, AdminHomeActivity::class.java)
        } else {
            Intent(this, UserHomeActivity::class.java).putExtra("USER_ID", id)
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String, id: Int) -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { FlightDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Flight System", style = MaterialTheme.typography.headlineLarge)
        Text("Login", style = MaterialTheme.typography.titleMedium)

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

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isEmpty()) {
                    message = "Please enter username and password"
                    return@Button
                }
                scope.launch {
                    isLoading = true
                    message = ""
                    val hashed = hashPassword(password)
                    val name = username.trim()

                    val admin = withContext(Dispatchers.IO) {
                        db.adminDao().getAdminByUsernameAndPassword(name, hashed)
                    }
                    if (admin != null) {
                        onLoginSuccess("admin", admin.id)
                    } else {
                        val user = withContext(Dispatchers.IO) {
                            db.userDao().getUserByUsernameAndPassword(name, hashed)
                        }
                        if (user != null) {
                            onLoginSuccess("user", user.id)
                        } else {
                            message = "Wrong username or password"
                        }
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Please wait..." else "Login")
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Don't have an account? Register")
        }
    }
}

// Call this from a Logout button anywhere in the app
fun Context.logout() {
    getSharedPreferences("session", Context.MODE_PRIVATE).edit().clear().apply()
    startActivity(
        Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    )
}