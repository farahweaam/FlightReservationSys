package com.example.flightsystem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    override val username: String,
    override val hashedpass: String,

    @ColumnInfo(defaultValue = "''")
    val email: String = "",

    @ColumnInfo(defaultValue = "''")
    val phone: String = ""
) : Person(username, hashedpass)