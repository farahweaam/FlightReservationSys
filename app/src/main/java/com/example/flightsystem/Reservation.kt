package com.example.flightsystem

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(User::class, ["id"], ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Flight::class, ["id"], ["flightId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["userId", "flightId"], unique = true),
        Index(value = ["flightId", "seatNumber"], unique = true)
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val flightId: Int,
    val classType: Int,
    val price: Double,
    val seatNumber: String? = null
)