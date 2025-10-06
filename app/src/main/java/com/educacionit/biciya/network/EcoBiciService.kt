package com.educacionit.biciya.network

import com.educacionit.biciya.models.response.StationInformationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EcoBiciService {

    @GET("stationInformation")
    suspend fun getStationInformation(@Query("client_id") clientId: String,
                                      @Query("client_secret") clientSecret: String): Response<StationInformationResponse>
}