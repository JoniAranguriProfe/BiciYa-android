package com.educacionit.biciya.home

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.educacionit.biciya.data.StationRepository
import com.educacionit.biciya.data.database.AppDatabase
import com.educacionit.biciya.data.database.StationDao
import com.educacionit.biciya.data.database.StationEntity
import com.educacionit.biciya.home.model.LocationProvider
import com.educacionit.biciya.network.ApiClient
import com.educacionit.biciya.utils.notification.NotificationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class WorkNotification(appContext: Context, workerParams: WorkerParameters) : Worker(appContext,workerParams) {

    //var lon: Double? = null
    //var lat: Double? = null
    private lateinit var dao : StationDao
    private lateinit var station: List<StationEntity>
    override fun doWork(): Result {

        dao = AppDatabase.Companion.getInstance(applicationContext).stationDao()

        CoroutineScope(Dispatchers.IO).launch {
            getStationLocation()
            val locationProvider = LocationProvider(WeakReference(applicationContext))
            locationProvider.subscribeToLocationUpdates(
                onaLocationUpdate = { location ->
                    locationProvider.unSuscribeToLocationUpdates()
                    processLocation(LatLng(location.latitude, location.longitude))
                },
                onFailure = {
                    Log.e("getLocation", "Error getting location")
                }
            )


        }

        return Result.success()
    }

    private fun calculateDistance(
        userLocation: LatLng
    ) : Boolean {

        if(station.isEmpty())
            return false

        for ( i in station.indices) {

            val result = FloatArray(1)
            val location = Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                station[i].lat,
                station[i].lon,
                result
            )

            if (result[0] < DISTANCE_METERS) {
                return true
            }

        }
        return false
    }

    private suspend fun getStationLocation() {
        station = StationRepository(dao).getAllStations()
    }
    private suspend fun updateStations(userLocation: LatLng)
    {
        val response = ApiClient.ecobiciService.getStationInformation()
        val stationListInformation = response.body()
        val stationList = stationListInformation?.data?.stations?.map{it.toStationEntity()}


        for ( i in stationList!!.indices)
        {
            val result = FloatArray(1)
            val location = Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                stationList[i].lat,
                stationList[i].lon,
                result
            )
            if (result[0] < DISTANCE_METERS) {
                StationRepository(dao).insertStation(stationList[i])
            }

        }

    }
    fun processLocation(userLocation: LatLng) {

        print("Obtuve usuario")
        print("userLocation: ${userLocation.longitude} - ${userLocation.latitude}")
        var isClose = calculateDistance(userLocation)
        if(isClose) {
            NotificationHelper.showRequestSavedNotification(applicationContext)
        }
        else
        {
            CoroutineScope(Dispatchers.IO).launch {
                updateStations(userLocation)
                getStationLocation()
            }
            isClose = calculateDistance(userLocation)
            if(isClose) {
                NotificationHelper.showRequestSavedNotification(applicationContext)
            }
        }

    }
    companion object
    {
        lateinit var work: WorkManager
        fun saveNotification( tag : String , duration: Long , ctx : Context)
        {

            val notificationWorkerRequest =
                PeriodicWorkRequestBuilder<WorkNotification>(duration, TimeUnit.MINUTES)
                    .addTag(tag)
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build()
            work = WorkManager.getInstance(ctx)
            work.enqueueUniquePeriodicWork("WorkNotification",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWorkerRequest)//enqueue(notificationWorkerRequest)

        }
        fun cancelNotification(tag : String)
        {
            work.cancelAllWork()
        }

        const val DISTANCE_METERS = 3000.0
    }
}