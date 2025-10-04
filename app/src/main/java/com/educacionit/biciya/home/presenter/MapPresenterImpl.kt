package com.educacionit.biciya.home.presenter

import com.educacionit.biciya.home.contracts.map.model.MapModel
import com.educacionit.biciya.home.contracts.map.MapPresenter
import com.educacionit.biciya.home.contracts.map.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapPresenterImpl(private val view : MapView,
                       private val model : MapModel
) : MapPresenter{
    override fun loadStations() {
        view.setLoadingVisibility(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val stations = model.getStations()
                CoroutineScope(Dispatchers.Main).launch {
                    view.showStationOnMap(stations!!.data.stations)
                    view.setLoadingVisibility(false)
                }
            } catch (e: Exception){
                CoroutineScope(Dispatchers.Main).launch {
                    view.showErrorMessage(e.message.toString())
                    view.setLoadingVisibility(false)
                }
            }
        }
    }
}