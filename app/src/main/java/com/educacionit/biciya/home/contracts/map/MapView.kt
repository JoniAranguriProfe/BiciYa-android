package com.educacionit.biciya.home.contracts.map

import com.educacionit.biciya.models.response.Station

interface MapView {
    fun initPresenter()
    fun showStationOnMap(stations: List<Station>)
    fun showErrorMessage(message: String)
    fun setLoadingVisibility(isVisible: Boolean)
}