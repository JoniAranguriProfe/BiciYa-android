package com.educacionit.biciya.home.contracts.map.model

import com.educacionit.biciya.data.network.models.response.StationInformationResponse

interface MapModel {
    suspend fun getStations() : StationInformationResponse?
}