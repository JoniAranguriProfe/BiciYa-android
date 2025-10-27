package com.educacionit.biciya.home.contracts.map.model

import com.educacionit.biciya.data.network.models.response.StationInformationResponse
import com.educacionit.biciya.data.network.EcoBiciService

class MapModelImpl(private val ecoBiciService: EcoBiciService) : MapModel {
    override suspend fun getStations(): StationInformationResponse? {
        val response =
            ecoBiciService.getStationInformation()
        return response.body()
    }
}