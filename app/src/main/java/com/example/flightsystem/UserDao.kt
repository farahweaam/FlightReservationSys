package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id= :id")
    suspend fun getUserByID(id: Int): User?

    @Query("""
    SELECT * FROM users
    WHERE username = :username
    AND hashedpass = :password
    LIMIT 1
    """)
    suspend fun getUserByUsernameAndPassword(username: String, password: String): User?

    @Delete
    suspend fun deleteUser(user: User)
}