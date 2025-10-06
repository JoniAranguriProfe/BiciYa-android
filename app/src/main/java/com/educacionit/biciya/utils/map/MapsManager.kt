package com.educacionit.biciya.utils.map

import com.educacionit.biciya.models.response.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsManager {

    fun addStationMarkers(map: GoogleMap, stations: List<Station>) {
        map.clear() //Limpio el mapa para que no queden estaciones previas marcadas en el mapa, revisar si se agregan mas marcas que NO deban limpiarse.
        val boundsBuilder = LatLngBounds.builder()
        for (station in stations) {
            val position = LatLng(station.lat, station.lon)
            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(station.name)
            )
            boundsBuilder.include(position)
        }

        val bounds = boundsBuilder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        map.animateCamera(cameraUpdate)
    }

    fun moveCameraToUserLocation(map: GoogleMap, position: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
    }

}