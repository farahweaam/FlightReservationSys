package com.example.flightsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaneDao {
    @Insert
    suspend fun insertPlane(plane: Plane)

    @Query("SELECT * FROM planes WHERE id= :id")
    suspend fun getPlaneByID(id: Int): Plane?

    @Query("SELECT * FROM planes")
    suspend fun getAllPlanes(): List<Plane>?

    @Update
    suspend fun updatePlane(plane: Plane)

    @Delete
    suspend fun deletePlane(plane: Plane)
}