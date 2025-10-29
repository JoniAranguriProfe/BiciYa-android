package com.educacionit.biciya.home

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.educacionit.biciya.data.StationRepository
import com.educacionit.biciya.data.database.AppDatabase
import com.educacionit.biciya.data.database.StationEntity
import com.educacionit.biciya.home.model.LocationProvider
import com.educacionit.biciya.network.ApiClient
import com.educacionit.biciya.network.EcoBiciService
import com.educacionit.biciya.utils.notification.NotificationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

interface DistanceCalculator {
    fun calculateDistance(distanceOne: LatLng, distanceTwo: LatLng): Float
}

object DistanceCalculatorImpl : DistanceCalculator {
    override fun calculateDistance(distanceOne: LatLng, distanceTwo: LatLng): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            distanceOne.latitude,
            distanceOne.longitude,
            distanceTwo.latitude,
            distanceTwo.longitude,
            result
        )
        return result.first()
    }

}

class WorkNotification(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private var distanceCalculator: DistanceCalculator = DistanceCalculatorImpl
    private lateinit var stationRepository: StationRepository
    private lateinit var ecoBiciService: EcoBiciService

    @VisibleForTesting
    internal var stations: List<StationEntity> = emptyList()

    constructor(
        stationRepository: StationRepository, ecoBiciService: EcoBiciService,
        distanceCalculator: DistanceCalculator,
        appContext: Context, workerParams: WorkerParameters
    ) : this(appContext, workerParams) {
        this.distanceCalculator = distanceCalculator
        this.stationRepository = stationRepository
        this.ecoBiciService = ecoBiciService
    }

    override fun doWork(): Result {
        stationRepository = StationRepository(
            dao = AppDatabase.Companion.getInstance(applicationContext).stationDao()
        )
        ecoBiciService = ApiClient.ecobiciService

        CoroutineScope(Dispatchers.IO).launch {
            getStationLocations()
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

    @VisibleForTesting
    suspend fun getStationLocations() {
        stations = stationRepository.getAllStations()
    }

    @VisibleForTesting
    suspend fun updateNearStations(userLocation: LatLng) {
        val response = ecoBiciService.getStationInformation()
        val stationList = response.body()?.data?.stations?.map { it.toStationEntity() }
        stationList?.let { stationListSafe ->
            stationListSafe.forEach {
                if (isNearToStation(userLocation, listOf(it))) {
                    stationRepository.insertStation(it)
                }
            }

        }

    }

    fun processLocation(userLocation: LatLng) {
        print("Obtuve usuario")
        print("userLocation: ${userLocation.longitude} - ${userLocation.latitude}")
        if (isNearToStation(userLocation, this.stations)) {
            NotificationHelper.showRequestSavedNotification(applicationContext)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                updateNearStations(userLocation)
                getStationLocations()
            }
            if (isNearToStation(userLocation, this.stations)) {
                NotificationHelper.showRequestSavedNotification(applicationContext)
            }
        }

    }

    private fun isNearToStation(
        userLocation: LatLng, stationList: List<StationEntity>
    ): Boolean {
        if (stationList.isEmpty())
            return false

        stationList.forEach {
            val distanceToCurrentStation =
                distanceCalculator.calculateDistance(userLocation, LatLng(it.lat, it.lon))
            if (distanceToCurrentStation < MAX_DISTANCE_METERS) {
                return true
            }
        }
        return false
    }

    companion object {
        lateinit var work: WorkManager
        fun saveNotification(tag: String, duration: Long, ctx: Context) {

            val notificationWorkerRequest =
                PeriodicWorkRequestBuilder<WorkNotification>(duration, TimeUnit.MINUTES)
                    .addTag(tag)
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build()
            work = WorkManager.getInstance(ctx)
            work.enqueueUniquePeriodicWork(
                "WorkNotification",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWorkerRequest
            )

        }

        fun cancelNotification(tag: String) {
            work.cancelAllWork()
        }

        const val MAX_DISTANCE_METERS = 3000.0
    }
}