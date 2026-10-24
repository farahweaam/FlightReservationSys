package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AdminDao {
    @Insert
    suspend fun insertAdmin(admin: Admin)

    @Query("""
    SELECT * FROM admins
    WHERE username = :username
    AND hashedpass = :password
    LIMIT 1
""")
    suspend fun getAdminByUsernameAndPassword(username: String, password: String): Admin?

    @Delete
    suspend fun deleteUser(user: User)
}