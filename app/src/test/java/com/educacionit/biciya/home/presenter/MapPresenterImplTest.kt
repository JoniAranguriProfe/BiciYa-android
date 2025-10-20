package com.educacionit.biciya.home.presenter

import com.educacionit.biciya.home.contracts.map.MapView
import com.educacionit.biciya.home.contracts.map.model.MapModel
import com.educacionit.biciya.models.response.Station
import com.educacionit.biciya.models.response.StationData
import com.educacionit.biciya.models.response.StationInformationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for the MapPresenterImpl class.
 */
@ExperimentalCoroutinesApi
class MapPresenterImplTest {
    private val view: MapView = mock()
    private val model: MapModel = mock()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var presenter: MapPresenterImpl

    @Before
    fun setUp() {
        presenter = MapPresenterImpl(view, model, CoroutineScope(testDispatcher))
    }

    @Test
    fun `loadStations SHOULD show stations on map WHEN model returns data successfully`() =
        runTest {
            val fakeStations = listOf(
                Station(
                    stationId = "1",
                    name = "Central Park Station",
                    lat = 40.7,
                    lon = -74.0,
                    address = "123 Park Ave",
                    crossStreet = "5th Ave",
                    postCode = "10021",
                    capacity = 30,
                    rentalMethods = listOf("KEY", "CREDITCARD"),
                    nearbyDistance = 0.1
                )
            )
            val fakeResponse = StationInformationResponse(
                last_updated = System.currentTimeMillis(),
                ttl = 60,
                data = StationData(stations = fakeStations)
            )
            whenever(model.getStations()).thenReturn(fakeResponse)

            presenter.loadStations()

            inOrder(view) {
                verify(view).setLoadingVisibility(true)
                verify(view).showStationOnMap(fakeStations)
                verify(view).setLoadingVisibility(false)
            }
            verify(view, never()).showErrorMessage(any())
        }

    @Test
    fun `loadStations SHOULD show error message WHEN model returns null`() = runTest {
        whenever(model.getStations()).thenReturn(null)

        presenter.loadStations()

        inOrder(view) {
            verify(view).setLoadingVisibility(true)
            verify(view).showErrorMessage("No se pudo obtener las estaciones o el servicio no está disponible!")
            verify(view).setLoadingVisibility(false)
        }
        verify(view, never()).showStationOnMap(any())
    }

    @Test
    fun `loadStations SHOULD show error message WHEN model throws an exception`() = runTest {
        val errorMessage = "Network request failed"
        whenever(model.getStations()).thenThrow(RuntimeException(errorMessage))

        presenter.loadStations()

        inOrder(view) {
            verify(view).setLoadingVisibility(true)
            verify(view).showErrorMessage(errorMessage)
            verify(view).setLoadingVisibility(false)
        }
        verify(view, never()).showStationOnMap(any())
    }
}