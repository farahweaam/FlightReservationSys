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

//sama
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = FlightDatabase.getInstance(applicationContext)
        val flightDao = db.flightDao()

        // comment here
        // maryam(testingggggg)

        lifecycleScope.launch(Dispatchers.IO) {

            // 1. Insert airports if the table is empty
            if (db.airportDao().getAllAirports().isNullOrEmpty()) {
                db.airportDao().insertAirport(
                    Airport(
                        name = "Cairo International",
                        location = "Cairo"
                    )
                )

                db.airportDao().insertAirport(
                    Airport(
                        name = "Dubai International",
                        location = "Dubai"
                    )
                )
            }

            // 2. Insert planes if the table is empty
            if (db.planeDao().getAllPlanes().isNullOrEmpty()) {
                db.planeDao().insertPlane(
                    Plane(
                        model = "Boeing 737",
                        aClassSeats = 20,
                        bClassSeats = 100,
                        cClassSeats = 30,
                        totalSeats = 150
                    )
                )

                db.planeDao().insertPlane(
                    Plane(
                        model = "Airbus A320",
                        aClassSeats = 15,
                        bClassSeats = 105,
                        cClassSeats = 30,
                        totalSeats = 150
                    )
                )
            }

            // 3. Create a test user if it doesn't exist
            var testUser = db.userDao()
                .getUserByUsernameAndPassword("reservation_test", "test_hash")

            if (testUser == null) {
                db.userDao().insertUser(
                    User(
                        username = "reservation_test",
                        hashedpass = "test_hash"
                    )
                )

                testUser = db.userDao()
                    .getUserByUsernameAndPassword("reservation_test", "test_hash")
            }

            // 4. Insert a test flight if there are no flights
            if (db.flightDao().getAllFlights().isEmpty()) {

                val airport = db.airportDao().getAllAirports()!!.first()
                val plane = db.planeDao().getAllPlanes()!!.first()

                val testFlight = Flight(
                    destination = "Dubai",
                    departure_time = System.currentTimeMillis() +
                            (24 * 60 * 60 * 1000),
                    airportId = airport.id,
                    planeId = plane.id,
                    aClassPrice = 300.0,
                    bClassPrice = 200.0,
                    cClassPrice = 100.0
                )

                db.flightDao().insertFlight(testFlight)
            }

            // 5. Open the user screen after inserting test data
            withContext(Dispatchers.Main) {
                setContent {
                    FlightSystemTheme {
                        UserHomeScreen(userId = testUser!!.id)
                    }
                }
            }
        }



        /*/sama(for testing AdminFlightsScreen)
        lifecycleScope.launch(Dispatchers.IO) {

            // Insert airports if the table is empty
            if (db.airportDao().getAllAirports().isNullOrEmpty()) {

                db.airportDao().insertAirport(
                    Airport(
                        name = "Cairo International",
                        location = "Cairo"
                    )
                )

                db.airportDao().insertAirport(
                    Airport(
                        name = "Dubai International",
                        location = "Dubai"
                    )
                )
            }

            // Insert planes if the table is empty
            if (db.planeDao().getAllPlanes().isNullOrEmpty()) {

                db.planeDao().insertPlane(
                    Plane(
                        model = "Boeing 737",
                        aClassSeats = 20,
                        bClassSeats = 100,
                        cClassSeats = 30,
                        totalSeats = 150
                    )
                )

                db.planeDao().insertPlane(
                    Plane(
                        model = "Airbus A320",
                        aClassSeats = 15,
                        bClassSeats = 105,
                        cClassSeats = 30,
                        totalSeats = 150
                    )
                )
            }
        }
        setContent {
            AdminFlightsScreen()
        }

         */

        /*/Sama : original setcontent fn
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
                            //  val timeInMillis = System.currentTimeMillis()

//                            val testFlight = Flight(
//                                destination = "Cairo to Dubai",
//                                departure_time = timeInMillis,
//                                airportId = 1,
//                                planeId = 1,
//                                aClassPrice = 300.0,
//                                bClassPrice = 200.0,
//                                cClassPrice = 100.0
//                            )
//                            db.airportDao().insertAirport(Airport(id = 1, name = "Cairo International", "Cairo, Egypt"))
//                            db.planeDao().insertPlane(Plane(id = 1, model = "Boeing 737", 12, 15, 32, 59))


                            // Insert into database
//                            flightDao.insertFlight(testFlight)

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
                            //text = "Flight Database Test",
                            text = "Flight Reservation System",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (flightList.isEmpty()) {
//                            Text(text = "Loading or empty database...")
                            Text(text = "No flights available yet.")
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
        }*/

        //Enas Testing//
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            if (db.airportDao().getAllAirports().isNullOrEmpty()) {
//                db.airportDao().insertAirport(Airport(name = "Cairo International", location = "Cairo"))
//                db.airportDao().insertAirport(Airport(name = "Dubai International", location = "Dubai"))
//            }
//
//            if (db.planeDao().getAllPlanes().isNullOrEmpty()) {
//                db.planeDao().insertPlane(
//                    Plane(model = "Boeing 737", aClassSeats = 20, bClassSeats = 100, cClassSeats = 30, totalSeats = 150)
//                )
//                db.planeDao().insertPlane(
//                    Plane(model = "Airbus A320", aClassSeats = 15, bClassSeats = 105, cClassSeats = 30, totalSeats = 150)
//                )
//            }
//
//            // a user to book for (type "reservation_test" in the booking screen)
//            if (db.userDao().getUserByUsername("reservation_test") == null) {
//                db.userDao().insertUser(User(username = "reservation_test", hashedpass = "test_hash"))
//            }
//
//            if (db.flightDao().getAllFlights().isEmpty()) {
//                val airport = db.airportDao().getAllAirports()!!.first()
//                val plane = db.planeDao().getAllPlanes()!!.first()
//                val day = 24 * 60 * 60 * 1000L
//
//                db.flightDao().insertFlight(
//                    Flight(
//                        destination = "Dubai",
//                        departure_time = System.currentTimeMillis() + day,
//                        airportId = airport.id,
//                        planeId = plane.id,
//                        aClassPrice = 300.0, bClassPrice = 200.0, cClassPrice = 100.0
//                    )
//                )
//                db.flightDao().insertFlight(
//                    Flight(
//                        destination = "London",
//                        departure_time = System.currentTimeMillis() + 3 * day,
//                        airportId = airport.id,
//                        planeId = plane.id,
//                        aClassPrice = 500.0, bClassPrice = 350.0, cClassPrice = 200.0
//                    )
//                )
//            }
//
//            withContext(Dispatchers.Main) {
//                setContent {
//                    FlightSystemTheme { AdminHomeScreen() }
//                }
//            }
//        }

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