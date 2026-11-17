
package com.example.flightsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

class AdminHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminHomeScreen() }
    }
}

@Composable
fun AdminHomeScreen() {
    var selectedFlight by remember { mutableStateOf<Flight?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            if (selectedFlight == null) {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {},
                        label = { Text("Book") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {},
                        label = { Text("Reservations") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {},
                        label = { Text("Flights") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { context.logout() },
                        icon = {},
                        label = { Text("Logout") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val flight = selectedFlight
            if (flight != null) {
                AdminBookingScreen(
                    flight = flight,
                    onReservationComplete = {
                        selectedFlight = null
                        selectedTab = 1
                    },
                    onBack = { selectedFlight = null }
                )
            } else {
                when (selectedTab) {
                    0 -> AvailableFlightsScreen(onFlightClick = { selectedFlight = it })
                    1 -> AdminReservationsScreen()
                    2 -> AdminFlightsScreen()
                }
            }
        }
    }
}

