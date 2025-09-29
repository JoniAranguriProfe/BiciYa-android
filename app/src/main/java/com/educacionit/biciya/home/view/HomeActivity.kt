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
import com.educacionit.biciya.home.view.fragments.MapFragment
import com.educacionit.biciya.home.view.fragments.RequestsFragment
import com.educacionit.biciya.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var mapFragment: MapFragment
    private lateinit var requestsFragment: RequestsFragment

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

    // TODO: Corregir comportamiento al girar la pantalla!
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
        // Get user location
        Toast.makeText(
            this,
            "Gracias por aceptar el permiso!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun explainWhyWeNeedAccessToLocation() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle("Necesitamos acceder a tu ubicacion!")
            dialog.setMessage("Parece que no aceptaste el acceso a ubicacion, quisieras modificar eso en la configuracion de la app?")
            dialog.setPositiveButton("Dale") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            dialog.setNegativeButton(
                "Cancel"
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
                hasLocationAccess(permissions) -> startGettingUserLocation()
                else -> explainWhyWeNeedAccessToLocation()
            }
        }
        locationPermissionRequest.launch(
            LOCATION_PERMISSIONS
        )
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

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}