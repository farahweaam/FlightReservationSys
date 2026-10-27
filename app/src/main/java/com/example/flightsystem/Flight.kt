package com.example.flightsystem
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "flights", foreignKeys = [
    ForeignKey(Airport::class, ["id"], ["airportId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(Plane::class, ["id"], ["planeId"], onDelete = ForeignKey.CASCADE)
])
data class Flight(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val destination:String,
    val departure_time: Long,
    val airportId:Int,
    val planeId: Int,
    val aClassPrice:Double,
    val bClassPrice: Double,
    val cClassPrice: Double

)
