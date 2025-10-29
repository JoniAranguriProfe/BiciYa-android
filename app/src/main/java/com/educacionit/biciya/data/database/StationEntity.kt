package com.educacionit.biciya.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class StationEntity(
    @PrimaryKey()
    val stationId: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val address: String?,
    val capacity: Int
)
