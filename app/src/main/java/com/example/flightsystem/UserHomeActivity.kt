
package com.example.flightsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
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

    if (selectedFlight == null) {

        AvailableFlightsScreen(
            onFlightClick = { flight ->
                selectedFlight = flight
            }
        )

    } else {

        FlightDetailsScreen(
            flight = selectedFlight!!,
            userId = userId,
            onReservationComplete = {
                selectedFlight = null
            },
            onBack = {
                selectedFlight = null
            }
        )
    }
}
