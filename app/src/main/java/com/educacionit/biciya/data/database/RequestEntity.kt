package com.educacionit.biciya.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
class RequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expirationDate: String,
    val distanceRange: Int,
    val bikesRequested: Int,
    val active: Boolean
)