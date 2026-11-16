
package com.example.flightsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
//samaaa
import android.content.Intent
import androidx.compose.ui.platform.LocalContext


class UserHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra("USER_ID", -1)

        setContent {
            UserHomeScreen(userId = userId)
        }
    }
}



@Composable
fun UserHomeScreen(userId: Int) {
//samaaa
    val context = LocalContext.current


    var selectedFlight by remember {
        mutableStateOf<Flight?>(null)
    }

    var selectedTab by remember {
        mutableStateOf(0)
    }

    Scaffold(
        bottomBar = {
            if (selectedFlight == null) {
                NavigationBar {

                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {},
                        label = { Text("Flights") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {},
                        label = { Text("Reservations") }
                    )

                    //samaaaa

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            context.startActivity(intent)
                        },
                        icon = {},
                        label = { Text("Profile") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {},
                        label = { Text("History") }
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (selectedFlight != null) {

                FlightDetailsScreen(
                    flight = selectedFlight!!,
                    userId = userId,
                    onReservationComplete = {
                        selectedFlight = null
                        selectedTab = 1
                    },
                    onBack = {
                        selectedFlight = null
                    }
                )

            } else {

                when (selectedTab) {

                    0 -> AvailableFlightsScreen(
                        onFlightClick = { flight ->
                            selectedFlight = flight
                        }
                    )

                    1 -> MyReservationsScreen(
                        userId = userId
                    )

                    2 -> ProfilePlaceholderScreen()

                    3 -> HistoryPlaceholderScreen()
                }
            }
        }
    }
}



@Composable
fun ProfilePlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profile Screen - Coming Soon",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun HistoryPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Booking History - Coming Soon",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

