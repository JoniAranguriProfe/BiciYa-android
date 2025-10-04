package com.educacionit.biciya.models.response

import com.google.gson.annotations.SerializedName

data class StationInformationResponse(
    val last_updated: Long,
    val ttl: Int,
    val data: StationData
)

data class StationData(
    val stations: List<Station>
)

data class Station(
    val station_id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val address: String?,
    val cross_street: String?,
    val post_code: String?,
    val capacity: Int,
    val rental_methods: List<String>,
    val nearby_distance: Double,
)
