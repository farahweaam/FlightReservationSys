
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FlightDetailsScreen(
    flight: Flight,
    userId: Int,
    onReservationComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FlightDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var selectedClass by remember { mutableStateOf(1) }
    var airportName by remember { mutableStateOf("Loading...") }
    var planeModel by remember { mutableStateOf("Loading...") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val price = when (selectedClass) {
        1 -> flight.aClassPrice
        2 -> flight.bClassPrice
        else -> flight.cClassPrice
    }

    // Get airport and plane information
    LaunchedEffect(flight.id) {
        withContext(Dispatchers.IO) {
            val airport = db.airportDao().getAirportByID(flight.airportId)
            val plane = db.planeDao().getPlaneByID(flight.planeId)

            airportName = airport?.name ?: "Unknown"
            planeModel = plane?.model ?: "Unknown"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Flight Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Destination: ${flight.destination}")

        Text(
            "Departure: ${
                SimpleDateFormat(
                    "dd/MM/yyyy - hh:mm a",
                    Locale.getDefault()
                ).format(Date(flight.departure_time))
            }"
        )

        Text("Airport: $airportName")
        Text("Plane: $planeModel")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Choose Your Class",
            style = MaterialTheme.typography.titleMedium
        )

        Row {
            RadioButton(
                selected = selectedClass == 1,
                onClick = { selectedClass = 1 }
            )
            Text("Class A - ${flight.aClassPrice}")
        }

        Row {
            RadioButton(
                selected = selectedClass == 2,
                onClick = { selectedClass = 2 }
            )
            Text("Class B - ${flight.bClassPrice}")
        }

        Row {
            RadioButton(
                selected = selectedClass == 3,
                onClick = { selectedClass = 3 }
            )
            Text("Class C - ${flight.cClassPrice}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Price: $price",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (userId <= 0) {
                    message = "Invalid user ID"
                } else {
                    scope.launch {
                        isLoading = true
                        message = ""


                        try {
                            val existingReservation = withContext(Dispatchers.IO) {
                                db.reservationDao().getReservationByUserAndFlight(
                                    userId,
                                    flight.id
                                )
                            }

                            if (existingReservation != null) {

                                message = "You have already booked this flight."

                            } else {

                                withContext(Dispatchers.IO) {
                                    db.reservationDao().insertReservation(
                                        Reservation(
                                            userId = userId,
                                            flightId = flight.id,
                                            classType = selectedClass,
                                            price = price
                                        )
                                    )
                                }

                                onReservationComplete()
                            }

                        } catch (e: Exception) {
                            message = "Reservation failed: ${e.message}"
                        }

                        isLoading = false

                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Please wait..." else "Confirm Reservation")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Flights")
        }
    }
}
