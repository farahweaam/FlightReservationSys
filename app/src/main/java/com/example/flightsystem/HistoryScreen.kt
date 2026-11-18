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
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(userId: Int) {
    val context = LocalContext.current
    val db = remember { FlightDatabase.getInstance(context) }

    var trips by remember { mutableStateOf<List<Pair<Reservation, Flight>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        isLoading = true
        trips = withContext(Dispatchers.IO) {
            val reservations = db.reservationDao().getReservationByUserID(userId) ?: emptyList()
            val flights = db.flightDao().getAllFlights()
            val now = System.currentTimeMillis()

            reservations
                .mapNotNull { r ->
                    flights.firstOrNull { it.id == r.flightId }?.let { f -> r to f }
                }
                .filter { (_, f) -> f.departure_time < now }
                .sortedByDescending { (_, f) -> f.departure_time }
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Booking History", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (trips.isEmpty()) {
            Text("No completed trips yet.")
        } else {
            Text("${trips.size} trip(s) · Total spent: ${trips.sumOf { it.first.price }}")
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(trips) { (reservation, flight) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                flight.destination,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Flew on: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy - hh:mm a",
                                        Locale.getDefault()
                                    ).format(Date(flight.departure_time))
                                }"
                            )
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
                        }
                    }
                }
            }
        }
    }
}