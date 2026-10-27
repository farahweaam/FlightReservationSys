package com.example.flightsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flightsystem.ui.theme.FlightSystemTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.room.Room

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = Room.databaseBuilder(
            applicationContext,
            FlightDatabase::class.java,
            "flight_db"
        ).build()
        val flightDao = db.flightDao()

        setContent {
            FlightSystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Insert parent records first so the foreign keys are valid

                    // State to hold the list of flights fetched from the database
                    var flightList by remember { mutableStateOf(listOf<Flight>()) }

                    // Insert a test flight and load the data when the screen launches
                    LaunchedEffect(key1 = Unit) {
                        // Run database tasks on an IO thread
                        kotlinx.coroutines.withContext(Dispatchers.IO) {
                            // Use system time in milliseconds directly (No java.time API level issues!)
                            val timeInMillis = System.currentTimeMillis()

                            val testFlight = Flight(
                                destination = "Cairo to Dubai",
                                departure_time = timeInMillis,
                                airportId = 1,
                                planeId = 1,
                                aClassPrice = 300.0,
                                bClassPrice = 200.0,
                                cClassPrice = 100.0
                            )
                            db.airportDao().insertAirport(Airport(id = 1, name = "Cairo International", "Cairo, Egypt"))
                            db.planeDao().insertPlane(Plane(id = 1, model = "Boeing 737", 12, 15, 32, 59))


                            // Insert into database
                            flightDao.insertFlight(testFlight)

                            // Fetch updated list
                            val fetchedFlights = flightDao.getAllFlights()

                            // Update state back on the main thread
                            withContext(Dispatchers.Main) {
                                flightList = fetchedFlights
                            }
                        }
                    }

                    // Simple Composable UI to display the loaded flights
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Flight Database Test",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (flightList.isEmpty()) {
                            Text(text = "Loading or empty database...")
                        } else {
                            for (flight in flightList) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "Destination: ${flight.destination}", style = MaterialTheme.typography.bodyLarge)
                                        Text(text = "Time (Millis): ${flight.departure_time}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "A Class Price: \$${flight.aClassPrice}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlightSystemTheme {
        Greeting("Android")
    }
}