
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

    var selectedFlight by remember {
        mutableStateOf<Flight?>(null)
    }

    var selectedTab by remember {
        mutableStateOf(0)
    }

    Scaffold(
        bottomBar = {
            if (selectedFlight == null) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Available Flights")
                    }

                    Button(
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("My Reservations")
                    }
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

                if (selectedTab == 0) {

                    AvailableFlightsScreen(
                        onFlightClick = { flight ->
                            selectedFlight = flight
                        }
                    )

                } else {

                    MyReservationsScreen(userId = userId)

                }
            }
        }
    }
}

