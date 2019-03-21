package com.rent24.driver.components

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.invoice.InvoiceFragment
import com.rent24.driver.components.job.JobFragment
import com.rent24.driver.components.job.list.JobListFragment
import com.rent24.driver.components.job.list.item.JobItemFragment
import com.rent24.driver.components.profile.ProfileFragment
import com.rent24.driver.components.snaps.SnapsFragment
import com.rent24.driver.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mMap: GoogleMap
    private var currentItem = 0
    private val onNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
                R.id.job -> {
                    fragment = onJobFragmentSelection()
                    return@OnNavigationItemSelectedListener replaceFragment(fragment)
                }
            }
            false
        }
    }
    private var status: Boolean = false
    private val onStatusCheckedChangeListener: CompoundButton.OnCheckedChangeListener by lazy {
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            binding.apiProgress
                .show()
            binding.model
                ?.callStatusApi(isChecked)
        }
    }
    private lateinit var switch: SwitchCompat
    private val onClickListener by lazy {
        object: JobListFragment.OnClickListener {
            override fun showDetailFragment(id: Int) {
                val fragment = JobItemFragment.newInstance()
                val bundle = Bundle()
                bundle.putInt("id", id)
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack("detail")
                    .commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        binding.navigation
            .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        binding.model = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(HomeViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupModel(binding.model!!)
        replaceFragment(onJobFragmentSelection())
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        val switchItem = menu?.findItem(R.id.job_status)!!
        switchItem.setActionView(R.layout.switch_toolbar_layout)

        switch = switchItem.actionView
            .findViewById(R.id.switch_toolbar)
        switch.setOnCheckedChangeListener(onStatusCheckedChangeListener)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.profile -> replaceFragment(onProfileFragmentSelection())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 &&
            supportFragmentManager.fragments
                .last() is JobItemFragment) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
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
        val fragment = InvoiceFragment.newInstance()
        // TODO: send scheduled job id
        val bundle = Bundle()
        bundle.putInt("jobId", 6)
        fragment.arguments = bundle
        return fragment
    }

    private fun onProfileFragmentSelection(): Fragment {
        return ProfileFragment.newInstance()
    }

    private fun onJobFragmentSelection(): Fragment {
        return JobFragment.newInstance(onClickListener)
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow()
        return true
    }

    private fun setupModel(model: HomeViewModel) {
        model.getStatus()
            .observe(this, Observer {
                status = it
                if (binding.apiProgress
                        .isShown) {
                    binding.apiProgress
                        .hide()
                }
                if (!status) {
                    switch.isChecked = false
                    Snackbar.make(binding.container, "Error occured, please try again", Snackbar.LENGTH_LONG)
                        .show()
                } else if (!switch.isChecked) {
                    Snackbar.make(binding.container, "You are offline", Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    Snackbar.make(binding.container, "You are online", Snackbar.LENGTH_LONG)
                        .show()
                }
            })
    }
}
