package com.example.flightsystem

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun dayKey(millis: Long): String =
    SimpleDateFormat("yyyyMMdd", Locale.US).format(Date(millis))

@Composable
fun AvailableFlightsScreen(
    onFlightClick: (Flight) -> Unit
) {
    val context = LocalContext.current
    val db = remember { FlightDatabase.getInstance(context) }

    var allFlights by remember { mutableStateOf<List<Flight>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var destinationFilter by remember { mutableStateOf("") }
    var maxPriceFilter by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        allFlights = withContext(Dispatchers.IO) { db.flightDao().getAllFlights() }
        isLoading = false
    }

    val now = System.currentTimeMillis()
    val maxPrice = maxPriceFilter.toDoubleOrNull()
    val filteredFlights = allFlights
        .filter { it.departure_time > now }
        .filter {
            destinationFilter.isBlank() ||
                    it.destination.contains(destinationFilter.trim(), ignoreCase = true)
        }
        .filter {
            maxPrice == null ||
                    minOf(it.aClassPrice, it.bClassPrice, it.cClassPrice) <= maxPrice
        }
        .filter {
            dateFilter == null || dayKey(it.departure_time) == dayKey(dateFilter!!)
        }
        .sortedBy { it.departure_time }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Available Flights", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = destinationFilter,
            onValueChange = { destinationFilter = it },
            label = { Text("Destination") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = maxPriceFilter,
                onValueChange = { maxPriceFilter = it },
                label = { Text("Max price") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    dateFilter?.let { cal.timeInMillis = it }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val picked = Calendar.getInstance()
                            picked.set(year, month, day, 0, 0, 0)
                            dateFilter = picked.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text(dateFilter?.let { dateFormat.format(Date(it)) } ?: "Any date")
            }
        }

        TextButton(
            onClick = {
                destinationFilter = ""
                maxPriceFilter = ""
                dateFilter = null
            }
        ) {
            Text("Clear filters")
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (allFlights.none { it.departure_time > now }) {
            Text("No flights available yet.")
        } else if (filteredFlights.isEmpty()) {
            Text("No flights match your filters.")
        } else {
            Text("${filteredFlights.size} flight(s) found")
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFlights) { flight ->
                    Card(
                        onClick = { onFlightClick(flight) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = flight.destination,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Departure: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy - hh:mm a",
                                        Locale.getDefault()
                                    ).format(Date(flight.departure_time))
                                }"
                            )
                            Text("Class A: ${flight.aClassPrice}")
                            Text("Class B: ${flight.bClassPrice}")
                            Text("Class C: ${flight.cClassPrice}")

                            Spacer(Modifier.height(8.dp))

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