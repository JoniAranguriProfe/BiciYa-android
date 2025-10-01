package com.educacionit.biciya.home.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.educacionit.biciya.R
import com.educacionit.biciya.utils.Constants
import com.educacionit.biciya.utils.LocationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mapFragment: MapFragment
    private lateinit var requestsFragment: RequestsFragment
    private val locationUtils = LocationUtils()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        when {
            // Primero chequeo si ya tengo los permisos
            LOCATION_PERMISSIONS.all { locationPermission ->
                ContextCompat.checkSelfPermission(
                    this,
                    locationPermission
                ) == PackageManager.PERMISSION_GRANTED
            } -> startGettingUserLocation()
            // Si no los tengo, y ya los habia pedido, muestro un mensaje de convencimiento
            LOCATION_PERMISSIONS.any {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, it
                )
            } -> explainWhyWeNeedAccessToLocation()
            // Si no los tengo y no los pedi, los pido
            else -> {
                requestPermissions()
            }
        }

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
        locationUtils.createLocationSettingsTask(
            this,
            Constants.REQUEST_CHECK_SETTINGS
        ) { location ->
            runOnUiThread {
                mapFragment.updateUserLocation(location.latitude, location.longitude)
            }
        }
    }

    private fun explainWhyWeNeedAccessToLocation() {
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


    private fun requestPermissions() {
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
}