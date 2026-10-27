package com.example.flightsystem

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Admin::class, User::class, Airport::class, Flight::class, Plane::class, Reservation::class], version = 1)
abstract class FlightDatabase: RoomDatabase() {
    abstract fun adminDao(): AdminDao

    abstract fun userDao(): UserDao

    abstract fun airportDao(): AirportDao

    abstract fun flightDao(): FlightDao

    abstract fun planeDao(): PlaneDao

    abstract fun reservationDao(): ReservationDao

    companion object{
        private var Instance: FlightDatabase? = null

        fun getInstance(context: Context): FlightDatabase{
            if(Instance== null)
            {
                synchronized(FlightDatabase::class){
                    Instance = Room.databaseBuilder(
                        context.applicationContext,
                        FlightDatabase::class.java,
                        "flightres_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return Instance!!
        }
    }
}