package com.educacionit.biciya.home.contracts.map.model

import com.educacionit.biciya.BuildConfig
import com.educacionit.biciya.models.response.StationInformationResponse
import com.educacionit.biciya.network.EcoBiciService

class MapModelImpl(private val ecoBiciService: EcoBiciService) : MapModel {
    override suspend fun getStations(): StationInformationResponse? {
        val response =
            ecoBiciService.getStationInformation()
        return response.body()
    }
}