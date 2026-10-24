package com.example.flightsystem
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val username: String,
    override val hashedpass: String
) : Person(username, hashedpass)