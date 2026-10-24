package com.example.flightsystem
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName ="airports")
data class Airport(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val name:String,
    val location: String
)
