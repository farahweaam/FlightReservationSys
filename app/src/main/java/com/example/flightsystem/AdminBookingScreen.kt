
package com.example.flightsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminBookingScreen(
    flight: Flight,
    onReservationComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FlightDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var message by remember { mutableStateOf("") }

    val userId = selectedUserId

    if (userId != null) {
        FlightDetailsScreen(
            flight = flight,
            userId = userId,
            onReservationComplete = onReservationComplete,
            onBack = { selectedUserId = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text("Book for a user", style = MaterialTheme.typography.headlineMedium)
            Text("Flight to ${flight.destination}")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("User's username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        val user = withContext(Dispatchers.IO) {
                            db.userDao().getUserByUsername(username.trim())
                        }
                        if (user == null) {
                            message = "User not found"
                        } else {
                            message = ""
                            selectedUserId = user.id
                        }
                    }
                },
                enabled = username.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Continue") }

            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error)
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Flights")
            }
        }
    }
}