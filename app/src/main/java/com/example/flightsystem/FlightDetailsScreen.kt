package com.example.flightsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color


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

    var selectedClass by remember { mutableIntStateOf(1) }
    var selectedSeat by remember { mutableStateOf<String?>(null) }

    var airportName by remember { mutableStateOf("Loading...") }
    var planeModel by remember { mutableStateOf("Loading...") }

    var plane by remember { mutableStateOf<Plane?>(null) }
    var reservedSeats by remember { mutableStateOf<Set<String>>(emptySet()) }

    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val price = when (selectedClass) {
        1 -> flight.aClassPrice
        2 -> flight.bClassPrice
        else -> flight.cClassPrice
    }

    // Load flight information and reserved seats
    LaunchedEffect(flight.id) {

        try {

            val result = withContext(Dispatchers.IO) {

                val airport = db.airportDao()
                    .getAirportByID(flight.airportId)

                val selectedPlane = db.planeDao()
                    .getPlaneByID(flight.planeId)

                val seats = db.reservationDao()
                    .getReservedSeats(flight.id)
                    .toSet()

                Triple(airport, selectedPlane, seats)
            }

            airportName = result.first?.name ?: "Unknown"
            plane = result.second
            planeModel = result.second?.model ?: "Unknown"
            reservedSeats = result.third

        } catch (e: Exception) {

            message = "Could not load flight information: ${e.message}"
        }
    }

    // Generate seats according to the selected class
    val seatPrefix = when (selectedClass) {
        1 -> "A"
        2 -> "B"
        else -> "C"
    }

    val seatCount = when (selectedClass) {
        1 -> plane?.aClassSeats ?: 0
        2 -> plane?.bClassSeats ?: 0
        else -> plane?.cClassSeats ?: 0
    }

    val seatOptions = (1..seatCount.coerceAtLeast(0))
        .map { "$seatPrefix$it" }

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

        // Class selection
        Text(
            text = "Choose Your Class",
            style = MaterialTheme.typography.titleMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            RadioButton(
                selected = selectedClass == 1,
                onClick = {
                    selectedClass = 1
                    selectedSeat = null
                    message = ""
                }
            )

            Text("Class A - ${flight.aClassPrice}")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            RadioButton(
                selected = selectedClass == 2,
                onClick = {
                    selectedClass = 2
                    selectedSeat = null
                    message = ""
                }
            )

            Text("Class B - ${flight.bClassPrice}")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            RadioButton(
                selected = selectedClass == 3,
                onClick = {
                    selectedClass = 3
                    selectedSeat = null
                    message = ""
                }
            )

            Text("Class C - ${flight.cClassPrice}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Price: $price",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Seat selection
        Text(
            text = "Choose Your Seat",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Seat legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(3.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text("Available", style = MaterialTheme.typography.labelSmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(3.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reserved", style = MaterialTheme.typography.labelSmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(3.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text("Selected", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (plane == null) {

            Text("Loading seats...")

        } else if (seatCount <= 0) {

            Text(
                "No seats are configured for this class.",
                color = MaterialTheme.colorScheme.error
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(seatOptions) { seat ->

                    val isReserved = seat in reservedSeats
                    val isSelected = seat == selectedSeat

                    val seatColor = when {
                        isReserved -> Color.Red

                        isSelected -> Color.Yellow

                        else -> Color.Green
                    }

                    val textColor = when {
                        isReserved ->
                            MaterialTheme.colorScheme.onErrorContainer

                        isSelected ->
                            MaterialTheme.colorScheme.onTertiaryContainer

                        else ->
                            MaterialTheme.colorScheme.onSecondaryContainer
                    }

                    Surface(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .clickable(enabled = !isReserved) {
                                selectedSeat = seat
                                message = ""
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = seatColor
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = seat,
                                color = textColor,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Selected Seat: ${selectedSeat ?: "None"}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm reservation
        Button(
            onClick = {

                if (userId <= 0) {

                    message = "Invalid user ID"

                } else if (selectedSeat == null) {

                    message = "Please select a seat first."

                } else {
                    val seatToBook= selectedSeat!!

                    scope.launch {

                        isLoading = true
                        message = ""

                        try {

                            val existingReservation =
                                withContext(Dispatchers.IO) {
                                    db.reservationDao()
                                        .getReservationByUserAndFlight(
                                            userId,
                                            flight.id
                                        )
                                }

                            if (existingReservation != null) {

                                message =
                                    "You have already booked this flight."

                            } else {

                                val seatAlreadyReserved =
                                    withContext(Dispatchers.IO) {
                                        seatToBook in db.reservationDao()
                                            .getReservedSeats(flight.id)
                                    }

                                if (seatAlreadyReserved) {

                                    reservedSeats =
                                        withContext(Dispatchers.IO) {
                                            db.reservationDao()
                                                .getReservedSeats(flight.id)
                                                .toSet()
                                        }

                                    selectedSeat = null

                                    message =
                                        "Sorry, this seat has just been reserved. Please choose another seat."

                                } else {

                                    withContext(Dispatchers.IO) {

                                        db.reservationDao()
                                            .insertReservation(
                                                Reservation(
                                                    userId = userId,
                                                    flightId = flight.id,
                                                    classType = selectedClass,
                                                    price = price,
                                                    seatNumber = seatToBook
                                                )
                                            )
                                    }

                                    onReservationComplete()
                                }
                            }

                        } catch (e: Exception) {

                            val errorMessage = e.message ?: ""

                            if (
                                errorMessage.contains(
                                    "UNIQUE constraint failed",
                                    ignoreCase = true
                                )
                            ) {

                                message =
                                    "This seat or flight has already been reserved. Please choose another seat."

                                reservedSeats =
                                    withContext(Dispatchers.IO) {
                                        db.reservationDao()
                                            .getReservedSeats(flight.id)
                                            .toSet()
                                    }

                                selectedSeat = null

                            } else {

                                message =
                                    "Reservation failed: ${e.message}"
                            }

                        } finally {

                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && selectedSeat != null
        ) {

            Text(
                if (isLoading) "Please wait..."
                else "Confirm Reservation"
            )
        }

        if (message.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Back to Flights")
        }
    }
}