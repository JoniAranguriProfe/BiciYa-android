package com.educacionit.biciya.utils.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.educacionit.biciya.R
import com.educacionit.biciya.data.database.AppDatabase
import com.educacionit.biciya.data.database.RequestEntity
import com.educacionit.biciya.data.network.ApiClientProvider
import com.educacionit.biciya.data.network.models.response.Station
import com.educacionit.biciya.utils.Constants
import com.educacionit.biciya.utils.notification.NotificationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class StationCheckService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = true
    private val delayService = 30_000L

    override fun onCreate() {
        super.onCreate()
        startForeground(
            1,
            NotificationHelper.createSimpleNotification(
                this,
                getString(R.string.searching_nearby_stations)
            )
        )

        coroutineScope.launch {
            while (isRunning) {
                checkNearbyStations()
                delay(delayService)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun checkNearbyStations() {
        try {
            val fused = LocationServices.getFusedLocationProviderClient(this)
            val location = fused.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
            ).await()

            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                val stations = getStationsByApi() ?: return
                val activeRequest = getActiveRequest() ?: return

                if (!validateDateOfRequest(activeRequest)) return

                val nearby = stations.filter { station ->
                    val distance = calculateDistance(userLatLng, LatLng(station.lat, station.lon))
                    distance <= activeRequest.distanceRange
                }

                nearby.firstOrNull()?.let { station ->
                    notifyNearbyStation(station)
                    desactiveRequest()
                    stopSelf()
                    isRunning = false
                    Log.e("StationCheckService", "Estación encontrada, servicio detenido")
                }
            }
        } catch (e: Exception) {
            Log.e("StationCheckService", "Error: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        isRunning = false
        coroutineScope.cancel()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        /*
        * Calculos matematicos de GPT. propenso a fallar
         */
        val R = 6371000.0
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(start.latitude)) *
                cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2).pow(2)
        return 2 * R * atan2(sqrt(a), sqrt(1 - a))
    }

    private suspend fun getStationsByApi(): List<Station>? {
        val response = ApiClientProvider.ecobiciService.getStationInformation()
        val stations = response.body()?.data?.stations
        return stations
    }

    private suspend fun getActiveRequest(): RequestEntity? {
        return AppDatabase.getInstance(this).requestDao().getActiveRequest()
    }

    private suspend fun desactiveRequest() {
        AppDatabase.getInstance(this).requestDao().deactivateActiveRequest()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyNearbyStation(station: Station) {
        NotificationHelper.showStationNearbyNotification(this, station)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notifyExpiredRequest() {
        NotificationHelper.showExpiredRequestNotification(this)
    }

    private suspend fun validateDateOfRequest(activeRequest: RequestEntity): Boolean {
        return try {
            val expirationMillis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = java.time.format.DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)
                val expirationDateTime =
                    java.time.LocalDateTime.parse(activeRequest.expirationDate, formatter)
                val zoneId = java.time.ZoneId.systemDefault()
                expirationDateTime.atZone(zoneId).toInstant().toEpochMilli()
            } else {
                val sdf =
                    java.text.SimpleDateFormat(Constants.DATE_FORMAT, java.util.Locale.getDefault())
                val date = sdf.parse(activeRequest.expirationDate)
                date?.time ?: 0L
            }

            val isStillValid = System.currentTimeMillis() <= expirationMillis

            if (!isStillValid) {
                desactiveRequest()
                notifyExpiredRequest()
                stopSelf()
                isRunning = false
                Log.e("validateDateOfRequest", "Request vencido, servicio detenido")
            }

            isStillValid
        } catch (e: Exception) {
            Log.e("validateDateOfRequest", "Error al parsear fecha: ${e.message}")
            false
        }
    }
}
