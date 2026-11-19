package com.example.flightsystem

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Admin::class,
        User::class,
        Airport::class,
        Flight::class,
        Plane::class,
        Reservation::class
    ],
    version = 4
)
abstract class FlightDatabase : RoomDatabase() {

    abstract fun adminDao(): AdminDao
    abstract fun userDao(): UserDao
    abstract fun airportDao(): AirportDao
    abstract fun flightDao(): FlightDao
    abstract fun planeDao(): PlaneDao
    abstract fun reservationDao(): ReservationDao

    companion object {

        private var Instance: FlightDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE users ADD COLUMN email TEXT NOT NULL DEFAULT ''"
                )

                db.execSQL(
                    "ALTER TABLE users ADD COLUMN phone TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "ALTER TABLE reservations ADD COLUMN seatNumber TEXT"
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_reservations_flightId_seatNumber
                    ON reservations(flightId, seatNumber)
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): FlightDatabase {

            if (Instance == null) {
                synchronized(FlightDatabase::class) {
                    Instance = Room.databaseBuilder(
                        context.applicationContext,
                        FlightDatabase::class.java,
                        "flightres_db"
                    )
                        .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                        .build()
                }
            }

            return Instance!!
        }
    }
}