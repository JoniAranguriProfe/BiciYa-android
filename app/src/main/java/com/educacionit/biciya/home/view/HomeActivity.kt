package com.educacionit.biciya.home.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.educacionit.biciya.R
import com.educacionit.biciya.home.contracts.home.home.HomePresenter
import com.educacionit.biciya.home.contracts.home.home.HomeView
import com.educacionit.biciya.home.model.LocationProvider
import com.educacionit.biciya.home.presenter.HomePresenterImpl
import com.educacionit.biciya.utils.Constants
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference


class HomeActivity : AppCompatActivity(), HomeView {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mapFragment: MapFragment
    private lateinit var requestsFragment: RequestsFragment
    private lateinit var homePresenter: HomePresenter
    private lateinit var frame_progress: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initPresenter()
        setUpViews()

        initFragments()

        setFragmentOrMapViewAsDefault(
            savedInstanceState?.getInt(
                Constants.STATE_INSTANCE,
                R.id.map_item
            ) ?: R.id.map_item
        )
    }

    override fun onStart() {
        super.onStart()
        homePresenter.checkLocationPermissions()
    }

    private fun initFragments() {
        mapFragment = MapFragment()
        requestsFragment = RequestsFragment()
    }

    private fun setFragmentOrMapViewAsDefault(itemSelected: Int) {
        bottomNavigation.selectedItemId = itemSelected
    }

    private fun setUpViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        frame_progress = findViewById(R.id.frame_progress)


        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map_item -> {
                    println("Se selecciono el mapa")
                    loadFragment(mapFragment)
                    true
                }

                R.id.requests_item -> {
                    println("Se selecciono solicitudes")
                    loadFragment(requestsFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragmentToLoad: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container, fragmentToLoad)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.STATE_INSTANCE, bottomNavigation.selectedItemId)
        Log.i("onSaveInstanceState", "Instancia almacenada")
    }

    private fun startGettingUserLocation() {
        homePresenter.subscribeToLocationUpdates()
    }

    override fun explainWhyWeNeedAccessToLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle(getString(R.string.request_location_permission_title))
            dialog.setMessage(getString(R.string.request_location_permission_message))
            dialog.setPositiveButton(getString(R.string.go)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            dialog.setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }


    private fun onUserJustAcceptedPermissions() {
        Toast.makeText(
            this,
            getString(R.string.thanks_for_accepting_permission),
            Toast.LENGTH_SHORT
        ).show()
        startGettingUserLocation()
    }

    private fun hasLocationAccess(permissions: Map<String, @JvmSuppressWildcards Boolean>?): Boolean {
        return permissions?.let {
            it.getOrDefault(
                Manifest.permission.ACCESS_FINE_LOCATION,
                false
            ) || it.getOrDefault(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                false
            )
        } ?: false
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                //Se activo la ubicación
                startGettingUserLocation()
            } else {
                //NO se activo la ubicación
                Toast.makeText(this, getString(R.string.request_location), Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun setLoadingVisibility(isVisible: Boolean) {
        frame_progress.isVisible = isVisible
    }

    override fun initPresenter() {
        val homeModel = LocationProvider(weakContext = WeakReference(this))
        homePresenter = HomePresenterImpl(homeView = this, homeModel = homeModel)
    }

    override fun areLocationPermissionsGranted(): Boolean {
        return LOCATION_PERMISSIONS.all { locationPermission ->
            ContextCompat.checkSelfPermission(
                this,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun werePermissionsAlreadyRejected(): Boolean {
        return LOCATION_PERMISSIONS.any {
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, it
            )
        }
    }

    override fun requestLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                hasLocationAccess(permissions) -> onUserJustAcceptedPermissions()
                else -> explainWhyWeNeedAccessToLocation()
            }
        }
        locationPermissionRequest.launch(
            LOCATION_PERMISSIONS
        )
    }

    override fun onNewLocationUpdate(latitude: Double, longitude: Double) {
        mapFragment.updateUserLocation(latitude, longitude)
    }

    override fun startExceptionResolution(resolvableApiException: ResolvableApiException) {
        resolvableApiException.startResolutionForResult(this, Constants.REQUEST_CHECK_SETTINGS)
    }
}