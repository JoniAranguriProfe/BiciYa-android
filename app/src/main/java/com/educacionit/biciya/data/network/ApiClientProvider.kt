package com.educacionit.biciya.data.network

import com.educacionit.biciya.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientProvider {

    val ecobiciService: EcoBiciService =
        getRetrofit(Constants.BASE_URL).create(EcoBiciService::class.java)


    fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}