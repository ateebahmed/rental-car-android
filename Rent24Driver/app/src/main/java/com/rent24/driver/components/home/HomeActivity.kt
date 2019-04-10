package com.rent24.driver.components.home

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.invoice.InvoiceFragment
import com.rent24.driver.components.job.JobFragment
import com.rent24.driver.components.job.list.CompletedJobListFragment
import com.rent24.driver.components.job.list.item.JobItemFragment
import com.rent24.driver.components.map.ParentMapFragment
import com.rent24.driver.components.profile.ProfileFragment
import com.rent24.driver.components.snaps.ParentSnapsFragment
import com.rent24.driver.databinding.ActivityHomeBinding
import com.rent24.driver.service.LocationDetectionService

private val TAG = HomeActivity::class.java.name
const val ACTIVE_JOB_MAP_REQUEST = 10

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var currentItem = 0
    private val onNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (currentItem == item.itemId) {
                return@OnNavigationItemSelectedListener false
            } else {
                currentItem = item.itemId
                val fragment: Fragment
                return@OnNavigationItemSelectedListener when (item.itemId) {
                    R.id.job_map -> {
                        fragment = mapFragment
                        replaceFragment(fragment)
                    }
                    R.id.snaps -> {
                        fragment = snapsFragment
                        replaceFragment(fragment)
                    }
                    R.id.invoice -> {
                        fragment = invoiceFragment
                        replaceFragment(fragment)
                    }
                    R.id.job -> {
                        fragment = jobFragment
                        replaceFragment(fragment)
                    }
                    else -> false
                }
            }
        }
    }
    private lateinit var switch: SwitchCompat
    private val onClickListener by lazy {
        object: CompletedJobListFragment.OnClickListener {
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
    private val viewModel by lazy {
        ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(HomeViewModel::class.java)
    }
    private val jobFragment by lazy { JobFragment.newInstance(onClickListener) }
    private val mapFragment by lazy { ParentMapFragment.newInstance() }
    private val snapsFragment by lazy { ParentSnapsFragment.newInstance() }
    private val invoiceFragment by lazy { InvoiceFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        binding.navigation
            .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSupportActionBar(binding.toolbar)

        setupModel(viewModel)
        replaceFragment(jobFragment)
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
        switch.setOnCheckedChangeListener(viewModel.statusOnCheckedChangeListener)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.profile -> replaceFragment(onProfileFragmentSelection())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
        if (null != intent) {
            if (ACTIVE_JOB_MAP_REQUEST == intent.getIntExtra("type", -1)) {
                replaceFragment(mapFragment)
                viewModel.setPickupLocation(intent.getDoubleArrayExtra("pickup").let {
                    LatLng(it[0], it[1])
                })
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 &&
            supportFragmentManager.fragments
                .last() is JobItemFragment) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
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

    private fun setupModel(model: HomeViewModel) {
        model.getStatus()
            .observe(this, Observer {
                if (!it) {
                    switch.isChecked = false
                }
            })
        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.container, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        model.getStartBackgroundService()
            .observe(this, Observer {
                if (it) {
                    startService(Intent(this, LocationDetectionService::class.java))
                } else {
                    stopService(Intent(this, LocationDetectionService::class.java))
                }
            })
        model.getShowLoadingProgressBar()
            .observe(this, Observer {
                if (it) {
                    binding.apiProgress
                        .show()
                } else {
                    binding.apiProgress
                        .hide()
                }
            })
    }

}
