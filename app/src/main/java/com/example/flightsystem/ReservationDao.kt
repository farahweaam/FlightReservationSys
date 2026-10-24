package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReservationDao {
    @Insert
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE userId= :uid")
    suspend fun getReservationByUserID(uid: Int): List<Reservation>?

    @Query("SELECT * FROM reservations WHERE userId= :uid LIMIT 1")
    suspend fun getLastReservationByUserID(uid: Int): Reservation?

    @Update
    suspend fun updateReservation(reservation: Reservation)

    @Delete
    suspend fun deleteReservation(reservation: Reservation)
}