package com.educacionit.biciya.home.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.educacionit.biciya.R
import com.educacionit.biciya.home.contracts.map.model.MapModelImpl
import com.educacionit.biciya.home.contracts.map.MapPresenter
import com.educacionit.biciya.home.contracts.map.MapView
import com.educacionit.biciya.home.presenter.MapPresenterImpl
import com.educacionit.biciya.models.response.Station
import com.educacionit.biciya.network.ApiClient
import com.educacionit.biciya.utils.map.MapsManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, MapView {

    private var googleMap: GoogleMap? = null
    private lateinit var presenter: MapPresenter
    private lateinit var frameProgress: FrameLayout
    private val mapsManager = MapsManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        initViews(view)
        initPresenter()
        return view
    }

    private fun initViews(view: View) {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        frameProgress = view.findViewById(R.id.frame_progress)
    }

    override fun onMapReady(updatedMap: GoogleMap) {
        googleMap = updatedMap
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true

        googleMap?.let { safeMap ->
            with(safeMap.uiSettings) {
                isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
            }
            // TODO: Use a single place to put the logic of permissions
            if (requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                safeMap.isMyLocationEnabled = true
            }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        if (!isAdded) {
            Log.e("updateUserLocation", "Fragment is not visible anymore")
            return
        }
        googleMap?.let {
            mapsManager.moveCameraToUserLocation(it, LatLng(lat, lng))
        } ?: Log.e("updateUserLocation", "googleMaps es nulo")

    }

    override fun initPresenter() {
        presenter = MapPresenterImpl(this@MapFragment, MapModelImpl(ApiClient.ecobiciService))
        lifecycleScope.launch {
            presenter.loadStations()
        }
    }

    override fun showStationOnMap(stations: List<Station>) {
        googleMap?.let { map ->
            mapsManager.addStationMarkers(map, stations)
        }
    }

    override fun showErrorMessage(message: String) {
        Log.e("showErrorMessage", message)
    }

    override fun setLoadingVisibility(isVisible: Boolean) {
        Log.e("setLoadingVisibility", isVisible.toString())
        frameProgress.isVisible = isVisible
    }
}