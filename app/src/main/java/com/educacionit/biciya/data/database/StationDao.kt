package com.educacionit.biciya.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StationDao {
    @Insert
    suspend fun insertStation(station: StationEntity)

    @Query("Select * from station")
    suspend fun getAllStations(): List<StationEntity>

}