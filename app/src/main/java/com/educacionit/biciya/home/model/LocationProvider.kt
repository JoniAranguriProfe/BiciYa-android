package com.educacionit.biciya.home.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.educacionit.biciya.home.contracts.home.HomeModel
import com.educacionit.biciya.home.WorkNotification
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import java.lang.ref.WeakReference

class LocationProvider(private val weakContext: WeakReference<Context?>) : HomeModel {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @SuppressLint("NewApi")
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(5000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    @SuppressLint("MissingPermission")
    override fun subscribeToLocationUpdates( //Si tiene la ubicación apagada, muestro Dialog para que la prenda, si esta activada la ubicación la obtengo del dispo
        onaLocationUpdate: (LatLng) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        weakContext.get()?.let { safeContext ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(safeContext)
            val locationRequest = createLocationRequest()

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            val client: SettingsClient = LocationServices.getSettingsClient(safeContext)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let {
                            onaLocationUpdate(LatLng(it.latitude, it.longitude))
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }

            task.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }
    }

    fun unSuscribeToLocationUpdates()
    {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}