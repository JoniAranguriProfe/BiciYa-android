package com.educacionit.biciya.home.presenter

import android.content.IntentSender
import com.educacionit.biciya.home.contracts.home.home.HomeModel
import com.educacionit.biciya.home.contracts.home.home.HomePresenter
import com.educacionit.biciya.home.contracts.home.home.HomeView
import com.google.android.gms.common.api.ResolvableApiException

class HomePresenterImpl(val homeView: HomeView, val homeModel: HomeModel) : HomePresenter {

    private val onLocationUpdateFailure: (Exception) -> Unit = { exception ->
        if (exception is ResolvableApiException) {
            try {
                homeView.startExceptionResolution(exception)
            } catch (sendEx: IntentSender.SendIntentException) {
                sendEx.printStackTrace()
            }
        }
    }

    override fun subscribeToLocationUpdates() {
        homeModel.subscribeToLocationUpdates(
            onaLocationUpdate = { location ->
                homeView.onNewLocationUpdate(location.latitude, location.longitude)
            },
            onFailure = onLocationUpdateFailure
        )
    }

    override fun checkLocationPermissions() {
        when {
            homeView.areLocationPermissionsGranted() -> subscribeToLocationUpdates()
            homeView.werePermissionsAlreadyRejected() -> homeView.explainWhyWeNeedAccessToLocation()
            else -> homeView.requestLocationPermissions()

        }
    }
}