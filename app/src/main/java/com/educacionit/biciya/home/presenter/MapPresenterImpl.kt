package com.educacionit.biciya.home.presenter

import com.educacionit.biciya.home.contracts.map.model.MapModel
import com.educacionit.biciya.home.contracts.map.MapPresenter
import com.educacionit.biciya.home.contracts.map.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapPresenterImpl(
    private val view: MapView,
    private val model: MapModel,
    private val presenterScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : MapPresenter {
    override suspend fun loadStations() {
        view.setLoadingVisibility(true)

        try {
            val stations = withContext(presenterScope.coroutineContext) {
                model.getStations()
            }

            stations?.let {
                view.showStationOnMap(it.data.stations)
            }
                ?: throw Exception("No se pudo obtener las estaciones o el servicio no está disponible!")
        } catch (e: Exception) {
            view.showErrorMessage(e.message.toString())
        } finally {
            view.setLoadingVisibility(false)
        }

    }
}