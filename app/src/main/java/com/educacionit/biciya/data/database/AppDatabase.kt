package com.educacionit.biciya.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RequestEntity::class , StationEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null
        private var NAME: String = "biciya_db"

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NAME
                ).fallbackToDestructiveMigration(false).build()
            }
            return INSTANCE!!
        }
    }

    abstract fun requestDao(): RequestDao

    abstract fun stationDao(): StationDao
}