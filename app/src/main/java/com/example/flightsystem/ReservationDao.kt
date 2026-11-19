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
    //maryam
    @Query("SELECT * FROM reservations WHERE userId = :uid AND flightId = :fid LIMIT 1")
    suspend fun getReservationByUserAndFlight(uid: Int, fid: Int): Reservation?

    @Query(" SELECT seatNumber FROM reservations WHERE flightId = :fid AND seatNumber IS NOT NULL")
    suspend fun getReservedSeats(fid: Int): List<String>

    @Query("SELECT EXISTS( SELECT 1 FROM reservations WHERE flightId = :fid AND seatNumber = :seat)")
    suspend fun isSeatReserved(fid: Int, seat: String): Boolean

    @Query("SELECT * FROM reservations")
    suspend fun getAllReservations(): List<Reservation>



}