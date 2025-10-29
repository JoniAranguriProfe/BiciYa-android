package com.educacionit.biciya.home

import com.educacionit.biciya.data.StationRepository
import com.educacionit.biciya.data.database.StationEntity
import com.educacionit.biciya.models.response.Station
import com.educacionit.biciya.models.response.StationData
import com.educacionit.biciya.models.response.StationInformationResponse
import com.educacionit.biciya.network.EcoBiciService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import retrofit2.Response


class WorkNotificationTest {

    private lateinit var mockedRepository: StationRepository
    private lateinit var mockedAPIService: EcoBiciService
    private lateinit var mockedDistanceCalculator: DistanceCalculator
    private lateinit var workNotification: WorkNotification

    @Before
    fun setUp() {
        mockedRepository = mock(StationRepository::class.java)
        mockedAPIService = mock(EcoBiciService::class.java)
        mockedDistanceCalculator = mock(DistanceCalculator::class.java)
        workNotification = WorkNotification(
            stationRepository = mockedRepository,
            ecoBiciService = mockedAPIService,
            distanceCalculator = mockedDistanceCalculator,
            appContext = mock(),
            workerParams = mock()
        )

    }

    @Test
    fun `getStationLocation SHOULD update the station list WHEN the database is not empty`() =
        runTest {
            val fakeStationList =
                listOf(mock(StationEntity::class.java), mock(StationEntity::class.java))
            Mockito.`when`(mockedRepository.getAllStations()).thenReturn(fakeStationList)
            Assert.assertTrue(workNotification.stations.isEmpty())

            workNotification.getStationLocations()

            Mockito.verify(mockedRepository).getAllStations()
            Assert.assertTrue(workNotification.stations.isNotEmpty())
            Assert.assertEquals(fakeStationList, workNotification.stations)
        }

    @Test
    fun `getStationLocation SHOULD update the station list WHEN the database is empty`() =
        runTest {
            Mockito.`when`(mockedRepository.getAllStations()).thenReturn(emptyList())
            Assert.assertTrue(workNotification.stations.isEmpty())

            workNotification.getStationLocations()

            Mockito.verify(mockedRepository).getAllStations()
            Assert.assertTrue(workNotification.stations.isEmpty())
            Assert.assertEquals(emptyList<StationEntity>(), workNotification.stations)
        }


    @Test
    fun `updateNearStations SHOULD save the near stations WHEN the API return near stations`() =
        runTest {
            val mockedUserLocation = LatLng(-34.59242413, -58.37470988)
            val nearMockedStation = Station(
                stationId = "2",
                name = "002 - Retiro I",
                lat = mockedUserLocation.latitude,
                lon = mockedUserLocation.longitude,
                address = "AV. Dr. José María Ramos Mejía 1300",
                crossStreet = null,
                postCode = null,
                capacity = 4,
                rentalMethods = mock(),
                nearbyDistance = 4.0,
            )
            val stationResponseMock = mock<StationInformationResponse>()
            val stationResponseDataMock = mock<StationData>()
            val stationListMock = listOf(
                nearMockedStation,
            )
            Mockito.`when`(stationResponseMock.data).thenReturn(stationResponseDataMock)
            Mockito.`when`(stationResponseDataMock.stations).thenReturn(stationListMock)
            Mockito.`when`(mockedAPIService.getStationInformation())
                .thenReturn(Response.success(stationResponseMock))
            Mockito.`when`(mockedDistanceCalculator.calculateDistance(any(),any())).thenReturn(10f)

            workNotification.updateNearStations(mockedUserLocation)

            Mockito.verify(mockedRepository).insertStation(nearMockedStation.toStationEntity())

        }

}