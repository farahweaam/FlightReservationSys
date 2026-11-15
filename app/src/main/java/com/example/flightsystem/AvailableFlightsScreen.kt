
package com.example.flightsystem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AvailableFlightsScreen(
    onFlightClick: (Flight) -> Unit
) {
    val context = LocalContext.current
    val db = FlightDatabase.getInstance(context)

    var flights by remember { mutableStateOf<List<Flight>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load flights from database
    LaunchedEffect(Unit) {
        flights = withContext(Dispatchers.IO) {
            db.flightDao().getAllFlights()
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Available Flights",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()

        } else if (flights.isEmpty()) {
            Text("No flights available yet.")

        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(flights) { flight ->

                    Card(
                        onClick = { onFlightClick(flight) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = flight.destination,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "Departure: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy - hh:mm a",
                                        Locale.getDefault()
                                    ).format(Date(flight.departure_time))
                                }"
                            )

                            Text("Class A: ${flight.aClassPrice}")
                            Text("Class B: ${flight.bClassPrice}")
                            Text("Class C: ${flight.cClassPrice}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "View Details",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
