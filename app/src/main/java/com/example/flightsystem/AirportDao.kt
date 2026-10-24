package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AirportDao {
    @Insert
    suspend fun insertAirport(airport: Airport)

    @Query("SELECT * FROM airports WHERE id= :id")
    suspend fun getAirportByID(id: Int): Airport?

    @Query("SELECT * FROM airports")
    suspend fun getAllAirports(): List<Airport>?

    @Update
    suspend fun updateAirport(airport: Airport)

    @Delete
    suspend fun deleteAirport(airport: Airport)

}