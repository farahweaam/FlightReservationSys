
package com.example.flightsystem

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlightsScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { FlightDatabase.getInstance(context) }

    var destination by remember { mutableStateOf("") }
    var airports by remember { mutableStateOf<List<Airport>>(emptyList()) }
    var planes by remember { mutableStateOf<List<Plane>>(emptyList()) }

    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var selectedPlane by remember { mutableStateOf<Plane?>(null) }

    var airportExpanded by remember { mutableStateOf(false) }
    var planeExpanded by remember { mutableStateOf(false) }

    var aClassPrice by remember { mutableStateOf("") }
    var bClassPrice by remember { mutableStateOf("") }
    var cClassPrice by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    var departureDate by remember { mutableStateOf("") }
    var departureTime by remember { mutableStateOf("") }

    var flights by remember { mutableStateOf<List<Flight>>(emptyList()) }
    var message by remember { mutableStateOf("") }


    suspend fun loadFlights() {
        flights = withContext(Dispatchers.IO) {
            db.flightDao().getUpcomingFlights(
                System.currentTimeMillis()
            )
        }
    }



    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            flights = db.flightDao().getUpcomingFlights(
                System.currentTimeMillis()
            )

            airports = db.airportDao().getAllAirports() ?: emptyList()
            planes = db.planeDao().getAllPlanes() ?: emptyList()
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "Flight Management",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Add New Flight",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = airportExpanded,
            onExpandedChange = { airportExpanded = !airportExpanded }
        ) {
            OutlinedTextField(
                value = selectedAirport?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Airport") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = airportExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = airportExpanded,
                onDismissRequest = { airportExpanded = false }
            ) {
                airports.forEach { airport ->
                    DropdownMenuItem(
                        text = { Text(airport.name) },
                        onClick = {
                            selectedAirport = airport
                            airportExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = planeExpanded,
            onExpandedChange = { planeExpanded = !planeExpanded }
        ) {
            OutlinedTextField(
                value = selectedPlane?.model ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Plane") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = planeExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = planeExpanded,
                onDismissRequest = { planeExpanded = false }
            ) {
                planes.forEach { plane ->
                    DropdownMenuItem(
                        text = { Text(plane.model) },
                        onClick = {
                            selectedPlane = plane
                            planeExpanded = false
                        }
                    )
                }
            }

        }
        if (airports.isEmpty()) {
            Text("No airports found. Add airports first.")
        }

        if (planes.isEmpty()) {
            Text("No planes found. Add planes first.")
        }

        OutlinedTextField(
            value = aClassPrice,
            onValueChange = { aClassPrice = it },
            label = { Text("A Class Price") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bClassPrice,
            onValueChange = { bClassPrice = it },
            label = { Text("B Class Price") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cClassPrice,
            onValueChange = { cClassPrice = it },
            label = { Text("C Class Price") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)

                        departureDate = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (departureDate.isEmpty())
                    "Select Departure Date"
                else departureDate
            )
        }

        Button(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        departureTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            hour,
                            minute
                        )
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (departureTime.isEmpty())
                    "Select Departure Time"
                else departureTime
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val airport = selectedAirport
                val plane = selectedPlane
                val priceA = aClassPrice.toDoubleOrNull()
                val priceB = bClassPrice.toDoubleOrNull()
                val priceC = cClassPrice.toDoubleOrNull()

                if (
                    destination.isBlank() ||
                    airport == null ||
                    plane == null ||
                    priceA == null ||
                    priceB == null ||
                    priceC == null ||
                    departureDate.isEmpty() ||
                    departureTime.isEmpty()
                ) {
                    message = "Please complete all fields correctly."
                } else {
                    val newFlight = Flight(
                        destination = destination,
                        departure_time = calendar.timeInMillis,
                        airportId = airport.id,
                        planeId = plane.id,
                        aClassPrice = priceA,
                        bClassPrice = priceB,
                        cClassPrice = priceC
                    )

                    scope.launch {
                        try {
                            val airportExists = withContext(Dispatchers.IO) {
                                db.airportDao().getAirportByID(airport.id)
                            }

                            val planeExists = withContext(Dispatchers.IO) {
                                db.planeDao().getPlaneByID(plane.id)
                            }

                            if (airportExists == null) {
                                message = "Airport ID $airport does not exist!"
                            } else if (planeExists == null) {
                                message = "Plane ID $plane does not exist!"
                            } else {

                                withContext(Dispatchers.IO) {
                                    db.flightDao().insertFlight(newFlight)
                                }

                                loadFlights()

                                destination = ""
                                selectedAirport = null
                                selectedPlane = null
                                aClassPrice = ""
                                bClassPrice = ""
                                cClassPrice = ""
                                departureDate = ""
                                departureTime = ""

                                message = "Flight added successfully!"
                            }

                        } catch (e: Exception) {
                            message = "Error adding flight: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Flight")
        }

        if (message.isNotEmpty()) {
            Text(message)
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "Manage Flights",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (flights.isEmpty()) {
            Text("No flights available yet.")
        } else {
            flights.forEach { flight ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text("Flight ID: ${flight.id}")
                        Text("Destination: ${flight.destination}")

                        Text(
                            "Departure: " +
                                    SimpleDateFormat(
                                        "dd/MM/yyyy HH:mm",
                                        Locale.getDefault()
                                    ).format(flight.departure_time)
                        )

                        Text("Airport ID: ${flight.airportId}")
                        Text("Plane ID: ${flight.planeId}")
                        Text("A Class: ${flight.aClassPrice}")
                        Text("B Class: ${flight.bClassPrice}")
                        Text("C Class: ${flight.cClassPrice}")

                        Button(
                            onClick = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.flightDao().deleteFlight(flight)
                                    }
                                    loadFlights()
                                }
                            }
                        ) {
                            Text("Delete Flight")
                        }
                    }
                }
            }
        }
    }
}