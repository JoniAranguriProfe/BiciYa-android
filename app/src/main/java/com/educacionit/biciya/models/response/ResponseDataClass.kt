package com.educacionit.biciya.models.response

import com.educacionit.biciya.data.database.StationEntity
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
    @SerializedName("station_id")
    val stationId: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val address: String?,
    @SerializedName("cross_street")
    val crossStreet: String?,
    @SerializedName("post_code")
    val postCode: String?,
    val capacity: Int,
    @SerializedName("rental_methods")
    val rentalMethods: List<String>,
    @SerializedName("nearby_distance")
    val nearbyDistance: Double,
){
    fun toStationEntity(): StationEntity = StationEntity(stationId,name,lat,lon,address,capacity)
}
