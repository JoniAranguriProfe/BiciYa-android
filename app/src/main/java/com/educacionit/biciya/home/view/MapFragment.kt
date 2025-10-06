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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
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

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        if (!isAdded || googleMap == null) {
            Log.e("updateUserLocation", "googleMaps es nulo")
            return
        }
        val position = LatLng(lat, lng)
        //googleMap?.clear() todo: Al actualizar la ubicación limpia el mapa, eso es correcto? se comenta por las dudas
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true

        if (requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
    }

    override fun initPresenter() {
        presenter = MapPresenterImpl(this@MapFragment, MapModelImpl(ApiClient.ecobiciService))
        lifecycleScope.launch {
            presenter.loadStations()
        }
    }

    override fun showStationOnMap(stations: List<Station>) {
        googleMap?.let { map ->
            map.clear() //Limpio el mapa para que no queden estaciones previas marcadas en el mapa, revisar si se agregan mas marcas que NO deban limpiarse.

            val boundsBuilder = LatLngBounds.builder()

            for (station in stations) {
                val position = LatLng(station.lat, station.lon)
                map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(station.name)
                )
                boundsBuilder.include(position)
            }

            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            map.animateCamera(cameraUpdate)
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