package com.educacionit.biciya.data

import com.educacionit.biciya.data.database.StationDao
import com.educacionit.biciya.data.database.StationEntity

class StationRepository (private val dao: StationDao){
    suspend fun insertStation(station: StationEntity) {
        dao.insertStation(station)
    }
    suspend fun getAllStations(): List<StationEntity> {
        return dao.getAllStations()
    }
}