package com.educacionit.biciya.home.contracts.map.model

import com.educacionit.biciya.BuildConfig
import com.educacionit.biciya.models.response.StationInformationResponse
import com.educacionit.biciya.network.ApiClient

class MapModelImpl : MapModel {
    override suspend fun getStations(): StationInformationResponse? {
        val response = ApiClient.ecobiciService.getStationInformation(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)
        return response.body()
    }
}