package com.example.flightsystem
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planes")
data class Plane(
    @PrimaryKey(autoGenerate = true) val id:Int =0,
    val model:String,
    val aClassSeats:Int,
    val bClassSeats:Int,
    val cClassSeats:Int,
    val totalSeats:Int
)
