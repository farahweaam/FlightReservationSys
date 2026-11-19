
package com.example.flightsystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FlightDatabase.getInstance(applicationContext)

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                // =================================
                // 1. CREATE ADMIN ACCOUNT
                // =================================

                if (db.adminDao().getAdminByUsername("admin") == null) {

                    db.adminDao().insertAdmin(
                        Admin(
                            username = "admin",
                            hashedpass = hashPassword("admin123")
                        )
                    )
                }

                // =================================
                // 2. INSERT TWO AIRPORTS
                // =================================

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

                // =================================
                // 3. INSERT TWO PLANES
                // =================================

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

                // =================================
                /*/ 4. CREATE TEST USERS
                // =================================

                if (db.userDao().getUserByUsername("testuser") == null) {

                    db.userDao().insertUser(
                        User(
                            username = "testuser",
                            hashedpass = hashPassword("user123"),
                            email = "testuser@gmail.com",
                            phone = "01012345678"
                        )
                    )
                }

                if (db.userDao().getUserByUsername("testuser2") == null) {

                    db.userDao().insertUser(
                        User(
                            username = "testuser2",
                            hashedpass = hashPassword("user123"),
                            email = "testuser2@gmail.com",
                            phone = "01112345678"
                        )
                    )
                }*/

                // =================================
                // 5. INSERT TEST FLIGHTS
                // =================================

                if (db.flightDao().getAllFlights().isEmpty()) {

                    val airports =
                        db.airportDao().getAllAirports() ?: emptyList()

                    val planes =
                        db.planeDao().getAllPlanes() ?: emptyList()

                    val cairoAirport = airports.firstOrNull {
                        it.name == "Cairo International"
                    }

                    val dubaiAirport = airports.firstOrNull {
                        it.name == "Dubai International"
                    }

                    val boeing = planes.firstOrNull {
                        it.model == "Boeing 737"
                    }

                    val airbus = planes.firstOrNull {
                        it.model == "Airbus A320"
                    }

                    if (cairoAirport != null &&
                        dubaiAirport != null &&
                        boeing != null &&
                        airbus != null
                    ) {

                        val now = System.currentTimeMillis()
                        val oneDay = 24 * 60 * 60 * 1000L

                        // Past flight: should NOT appear in admin lists
                        db.flightDao().insertFlight(
                            Flight(
                                destination = "Dubai",
                                departure_time = now - (2 * oneDay),
                                airportId = cairoAirport.id,
                                planeId = boeing.id,
                                aClassPrice = 300.0,
                                bClassPrice = 200.0,
                                cClassPrice = 100.0
                            )
                        )

                        // Upcoming flight 1
                        db.flightDao().insertFlight(
                            Flight(
                                destination = "Dubai",
                                departure_time = now + oneDay,
                                airportId = cairoAirport.id,
                                planeId = boeing.id,
                                aClassPrice = 300.0,
                                bClassPrice = 200.0,
                                cClassPrice = 100.0
                            )
                        )

                        // Upcoming flight 2
                        db.flightDao().insertFlight(
                            Flight(
                                destination = "London",
                                departure_time = now + (3 * oneDay),
                                airportId = cairoAirport.id,
                                planeId = airbus.id,
                                aClassPrice = 500.0,
                                bClassPrice = 350.0,
                                cClassPrice = 200.0
                            )
                        )

                        // Upcoming flight 3
                        db.flightDao().insertFlight(
                            Flight(
                                destination = "Paris",
                                departure_time = now + (5 * oneDay),
                                airportId = dubaiAirport.id,
                                planeId = airbus.id,
                                aClassPrice = 450.0,
                                bClassPrice = 300.0,
                                cClassPrice = 150.0
                            )
                        )
                    }
                }
            }

            // =================================
            // 6. OPEN NORMAL LOGIN SCREEN
            // =================================

            // Clear any previous login session
            getSharedPreferences("session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

// Open Login page
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
}
