package com.educacionit.biciya.home.contracts.request

import android.content.Context

interface RequestPresenter {
    suspend fun saveRequest(expirationDate: String, distance: Int, bikes: Int)
    suspend fun getInactiveRequests()
    suspend fun getActiveRequest()
    fun notifyRequestSaved(context: Context)
}