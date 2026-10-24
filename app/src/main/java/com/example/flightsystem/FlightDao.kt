package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlightDao {
    @Insert
    suspend fun insertFlight(flight: Flight)

    @Query("SELECT * FROM flights WHERE destination= :dest")
    suspend fun getFlightsToDestination(dest: String): List<Flight>?

    @Query("SELECT * FROM flights")
    suspend fun getAllFlights(): List<Flight>

    @Update
    suspend fun updateFlight(flight: Flight)

    @Delete
    suspend fun deleteFlight(flight: Flight)
}