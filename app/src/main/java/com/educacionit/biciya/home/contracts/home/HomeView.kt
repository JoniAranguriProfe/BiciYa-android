package com.educacionit.biciya.home.contracts.home

import com.google.android.gms.common.api.ResolvableApiException

interface HomeView {
    fun showErrorMessage(message: String)
    fun setLoadingVisibility(isVisible: Boolean)
    fun initPresenter()
    fun areLocationPermissionsGranted(): Boolean
    fun werePermissionsAlreadyRejected(): Boolean
    fun requestLocationPermissions()
    fun onNewLocationUpdate(latitude: Double, longitude: Double)
    fun startExceptionResolution(resolvableApiException: ResolvableApiException)
    fun explainWhyWeNeedAccessToLocation()
}