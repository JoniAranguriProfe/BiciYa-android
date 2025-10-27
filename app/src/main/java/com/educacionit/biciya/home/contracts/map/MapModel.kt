package com.educacionit.biciya.home.contracts.map

import com.educacionit.biciya.data.network.models.response.StationInformationResponse

interface MapModel {
    suspend fun getStations() : StationInformationResponse?
}