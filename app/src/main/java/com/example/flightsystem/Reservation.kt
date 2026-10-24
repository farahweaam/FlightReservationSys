package com.example.flightsystem
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "reservations", foreignKeys = [
    ForeignKey(User::class, ["id"], ["userId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(Flight::class, ["id"], ["flightId"], onDelete = ForeignKey.CASCADE)
])
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId:Int,
    val flightId:Int,
    val classType:Int,
    val price:Double
)
