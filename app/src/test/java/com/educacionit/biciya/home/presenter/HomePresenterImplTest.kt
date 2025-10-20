package com.educacionit.biciya.home.presenter

import com.educacionit.biciya.home.contracts.home.HomeModel
import com.educacionit.biciya.home.contracts.home.HomeView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for the HomePresenterImpl class.
 */
class HomePresenterImplTest {

    private val homeView: HomeView = mock()
    private val homeModel: HomeModel = mock()

    private lateinit var presenter: HomePresenterImpl

    @Before
    fun setUp() {
        presenter = HomePresenterImpl(homeView, homeModel)
    }

    @Test
    fun `checkLocationPermissions SHOULD subscribe to updates WHEN permissions are already granted`() {
        whenever(homeView.areLocationPermissionsGranted()).thenReturn(true)

        presenter.checkLocationPermissions()

        verify(homeModel).subscribeToLocationUpdates(any(), any())
        verify(homeView, never()).explainWhyWeNeedAccessToLocation()
        verify(homeView, never()).requestLocationPermissions()
    }

    @Test
    fun `checkLocationPermissions SHOULD explain the need for permissions WHEN they were already rejected`() {
        whenever(homeView.areLocationPermissionsGranted()).thenReturn(false)
        whenever(homeView.werePermissionsAlreadyRejected()).thenReturn(true)

        presenter.checkLocationPermissions()

        verify(homeView).explainWhyWeNeedAccessToLocation()
        verify(homeModel, never()).subscribeToLocationUpdates(any(), any())
        verify(homeView, never()).requestLocationPermissions()
    }

    @Test
    fun `checkLocationPermissions SHOULD request permissions WHEN it is the first time`() {
        whenever(homeView.areLocationPermissionsGranted()).thenReturn(false)
        whenever(homeView.werePermissionsAlreadyRejected()).thenReturn(false)

        presenter.checkLocationPermissions()

        verify(homeView).requestLocationPermissions()
        verify(homeModel, never()).subscribeToLocationUpdates(any(), any())
        verify(homeView, never()).explainWhyWeNeedAccessToLocation()
    }

    @Test
    fun `subscribeToLocationUpdates SHOULD forward new location to view ON success`() {
        val latLng = LatLng(10.0, 20.0)
        val successCallbackCaptor = argumentCaptor<(LatLng) -> Unit>()

        presenter.subscribeToLocationUpdates()

        verify(homeModel).subscribeToLocationUpdates(successCallbackCaptor.capture(), any())

        successCallbackCaptor.firstValue.invoke(latLng)
        verify(homeView).onNewLocationUpdate(10.0, 20.0)
    }

    @Test
    fun `subscribeToLocationUpdates SHOULD ask view to start resolution WHEN failure is a ResolvableApiException`() {
        val exception = mock<ResolvableApiException>()
        val failureCallbackCaptor = argumentCaptor<(Exception) -> Unit>()

        presenter.subscribeToLocationUpdates()

        verify(homeModel).subscribeToLocationUpdates(any(), failureCallbackCaptor.capture())
        failureCallbackCaptor.firstValue.invoke(exception)
        verify(homeView).startExceptionResolution(exception)
    }

    @Test
    fun `subscribeToLocationUpdates SHOULD do nothing WHEN failure is a generic exception`() {
        val exception = RuntimeException("Some error")
        val failureCallbackCaptor = argumentCaptor<(Exception) -> Unit>()

        presenter.subscribeToLocationUpdates()

        verify(homeModel).subscribeToLocationUpdates(any(), failureCallbackCaptor.capture())
        failureCallbackCaptor.firstValue.invoke(exception)
        verify(homeView, never()).startExceptionResolution(any())
    }
}