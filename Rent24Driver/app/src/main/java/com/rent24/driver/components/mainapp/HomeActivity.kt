package com.rent24.driver.components.mainapp

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.RelativeLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.mainapp.fragments.ui.invoice.InvoiceFragment
import com.rent24.driver.components.mainapp.fragments.ui.profile.ProfileFragment
import com.rent24.driver.components.mainapp.fragments.ui.snaps.SnapsFragment
import com.rent24.driver.databinding.ActivityHomeBinding
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_splash.*

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mMap: GoogleMap
    private var currentItem = 0
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        splash_screen.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mHideHandler = Handler()
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (currentItem == item.itemId) {
            return@OnNavigationItemSelectedListener false
        }
        val fragment: Fragment
        when (item.itemId) {
            R.id.job_map -> {
                fragment = onMapFragmentSelection()
                return@OnNavigationItemSelectedListener replaceFragment(fragment)
            }
            R.id.snaps -> {
                fragment = onSnapsFragmentSelection()
                return@OnNavigationItemSelectedListener replaceFragment(fragment)
            }
            R.id.invoice -> {
                fragment = onInvoiceFragmentSelection()
                return@OnNavigationItemSelectedListener replaceFragment(fragment)
            }
            R.id.profile -> {
                fragment = onProfileFragmentSelection()
                return@OnNavigationItemSelectedListener replaceFragment(fragment)
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        startActivityForResult(Intent(applicationContext, LoginActivity::class.java), 1)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-37.814, 144.96332)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Melbourne"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)

        supportActionBar?.hide()
        mHideHandler.postDelayed(mHidePart2Runnable, 300.toLong())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        val item = menu?.findItem(R.id.job_status)!!
        item.setActionView(R.layout.switch_toolbar_layout)
        val switch = item.actionView
            .findViewById<SwitchCompat>(R.id.switch_toolbar)
        switch.setOnCheckedChangeListener { _, isChecked ->
            Snackbar.make(findViewById(R.id.container), "checked$isChecked", Snackbar.LENGTH_SHORT)
                .show()
        }

        return true
    }

    private fun onMapFragmentSelection(): SupportMapFragment {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = SupportMapFragment()
        mapFragment.getMapAsync(this)
        return mapFragment
    }

    private fun onSnapsFragmentSelection(): Fragment {
        return SnapsFragment.newInstance()
    }

    private fun onInvoiceFragmentSelection(): Fragment {
        return InvoiceFragment.newInstance()
    }

    private fun onProfileFragmentSelection(): Fragment {
        return ProfileFragment.newInstance()
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow()
        return true
    }
}
