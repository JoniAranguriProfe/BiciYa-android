package com.educacionit.biciya.network

import com.educacionit.biciya.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ecobiciService: ApiService = retrofit.create(ApiService::class.java)
}