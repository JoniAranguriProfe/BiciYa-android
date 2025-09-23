package com.educacionit.biciya.home.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        setFragmentOrMapViewAsDefault(savedInstanceState?.getInt(Constants.STATE_INSTANCE, R.id.map_item) ?: R.id.map_item)
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
}