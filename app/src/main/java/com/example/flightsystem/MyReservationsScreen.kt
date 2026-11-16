
package com.example.flightsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun MyReservationsScreen(userId: Int) {

    val context = LocalContext.current
    val db = FlightDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    var reservations by remember {
        mutableStateOf<List<Reservation>>(emptyList())
    }

    var flights by remember {
        mutableStateOf<List<Flight>>(emptyList())
    }

    var isLoading by remember { mutableStateOf(true) }
    var selectedReservation by remember {
        mutableStateOf<Reservation?>(null)
    }
    var message by remember { mutableStateOf("") }

    // Load reservations and flights
    LaunchedEffect(userId) {
        isLoading = true

        val data = withContext(Dispatchers.IO) {
            val userReservations =
                db.reservationDao().getReservationByUserID(userId)
                    ?: emptyList()

            val allFlights = db.flightDao().getAllFlights()

            Pair(userReservations, allFlights)
        }

        reservations = data.first
        flights = data.second
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {

            CircularProgressIndicator()

        } else if (reservations.isEmpty()) {

            Text("You have no reservations yet.")

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(reservations) { reservation ->

                    val flight = flights.firstOrNull {
                        it.id == reservation.flightId
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = flight?.destination ?: "Flight not found",
                                style = MaterialTheme.typography.titleLarge
                            )

                            if (flight != null) {
                                Text(
                                    text = "Departure: ${
                                        SimpleDateFormat(
                                            "dd/MM/yyyy - hh:mm a",
                                            Locale.getDefault()
                                        ).format(Date(flight.departure_time))
                                    }"
                                )
                            }

                            Text("Reservation ID: ${reservation.id}")

                            Text(
                                "Class: ${
                                    when (reservation.classType) {
                                        1 -> "A"
                                        2 -> "B"
                                        else -> "C"
                                    }
                                }"
                            )

                            Text("Price: ${reservation.price}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    selectedReservation = reservation
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel Reservation")
                            }
                        }
                    }
                }
            }
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    // Ask for confirmation before deleting
    if (selectedReservation != null) {

        AlertDialog(
            onDismissRequest = {
                selectedReservation = null
            },
            title = {
                Text("Cancel Reservation?")
            },
            text = {
                Text("Are you sure you want to cancel this reservation?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val reservation = selectedReservation!!

                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    db.reservationDao()
                                        .deleteReservation(reservation)
                                }

                                reservations = reservations.filter {
                                    it.id != reservation.id
                                }

                                message = "Reservation cancelled successfully."

                            } catch (e: Exception) {
                                message = "Cancellation failed: ${e.message}"
                            }

                            selectedReservation = null
                        }
                    }
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedReservation = null
                    }
                ) {
                    Text("Keep Reservation")
                }
            }
        )
    }
}
