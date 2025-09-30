package com.educacionit.biciya.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

class LocationUtils {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("NewApi")
    fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(5000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun createLocationSettingsTask(context: Context, onUpdate: (LatLng) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let {
                            onUpdate(LatLng(it.latitude, it.longitude))
                        }
                    }
                },
                Looper.getMainLooper()
            )
        }
        task.addOnFailureListener {
            println("Location failed")
        }
    }

    fun getLocationUpdates(context: Context, onUpdate: (LatLng) -> Unit) {
        createLocationSettingsTask(context) { update ->
            onUpdate(update)
        }
     }
}