package com.educacionit.biciya.home.contracts.home

import com.google.android.gms.maps.model.LatLng

interface HomeModel {
    fun subscribeToLocationUpdates(
        onaLocationUpdate: (LatLng) -> Unit,
        onFailure: (Exception) ->Unit
    )
}