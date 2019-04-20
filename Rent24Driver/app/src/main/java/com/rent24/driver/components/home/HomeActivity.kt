package com.rent24.driver.components.home

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
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
import com.rent24.driver.components.job.list.ScheduledJobListViewModel
import com.rent24.driver.components.job.list.item.JobItemFragment
import com.rent24.driver.components.map.ParentMapFragment
import com.rent24.driver.components.profile.ProfileFragment
import com.rent24.driver.components.snaps.ParentSnapsFragment
import com.rent24.driver.databinding.ActivityHomeBinding
import com.rent24.driver.service.LocationDetectionService

private val TAG = HomeActivity::class.java.name
const val ACTIVE_JOB_MAP_REQUEST = 10
const val STATUS_PICKUP = 20
const val STATUS_DROP_OFF = 21
const val STATUS_TRIP_STOP = 22
private const val JOB_FRAGMENT_TAG = "job"
private const val INVOICE_FRAGMENT_TAG = "invoice"
private const val MAP_FRAGMENT_TAG = "map"
private const val SNAPS_FRAGMENT_TAG = "snaps"
private const val PROFILE_FRAGMENT_TAG = "profile"

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var currentItem = 0
    private val onNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (currentItem == item.itemId) {
                return@OnNavigationItemSelectedListener false
            } else {
                currentItem = item.itemId
                return@OnNavigationItemSelectedListener when (item.itemId) {
                    R.id.job_map -> {
                        replaceFragment(MAP_FRAGMENT_TAG, null, false)
                    }
                    R.id.snaps -> {
                        replaceFragment(SNAPS_FRAGMENT_TAG, null, false)
                    }
                    R.id.invoice -> {
                        replaceFragment(INVOICE_FRAGMENT_TAG, null, false)
                    }
                    R.id.job -> {
                        replaceFragment(JOB_FRAGMENT_TAG, null, false)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentItem = savedInstanceState?.getInt("currentMenuSelection") ?: currentItem
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        binding.navigation
            .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSupportActionBar(binding.toolbar)

        setupModel(viewModel)
        binding.navigation.selectedItemId = if (0 != currentItem) currentItem else R.id.job
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)

        supportActionBar?.hide()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putInt("currentMenuSelection", binding.navigation.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.navigation.selectedItemId = savedInstanceState?.getInt("currentMenuSelection") ?: PreferenceManager
            .getDefaultSharedPreferences(this)
            .getInt("currentMenuSelection", R.id.job)
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
            R.id.profile -> {
                currentItem = R.id.profile
                replaceFragment(PROFILE_FRAGMENT_TAG, null, true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onResume() {
        super.onResume()
        binding.navigation.selectedItemId = PreferenceManager.getDefaultSharedPreferences(this)
            .getInt("currentMenuSelection", R.id.job)
        if (null != intent) {
            handleIntents(intent)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
    }

    override fun onPause() {

        with(PreferenceManager.getDefaultSharedPreferences(this)
            .edit()) {
            putInt("currentMenuSelection", binding.navigation.selectedItemId)
            apply()
        }
        super.onPause()
    }

    private fun replaceFragment(fragmentTag: String, arguments: Bundle?, addToBackstack: Boolean): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) ?: createNewFragmentInstance(fragmentTag)
        if (null != arguments) {
            fragment.arguments = arguments
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, fragmentTag)
            .setPrimaryNavigationFragment(fragment)
        if (addToBackstack) {
            transaction.addToBackStack(fragmentTag)
        }
        transaction.commit()
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

    private fun createNewFragmentInstance(fragmentTag: String): Fragment {
        return when (fragmentTag) {
            JOB_FRAGMENT_TAG -> JobFragment.newInstance(onClickListener)
            MAP_FRAGMENT_TAG -> ParentMapFragment.newInstance()
            SNAPS_FRAGMENT_TAG -> ParentSnapsFragment.newInstance()
            INVOICE_FRAGMENT_TAG -> InvoiceFragment.newInstance()
            PROFILE_FRAGMENT_TAG -> ProfileFragment.newInstance()
            else -> JobFragment.newInstance(onClickListener)
        }
    }

    private fun handleIntents(intent: Intent) {
        when (intent.getIntExtra("type", -1)) {
            ACTIVE_JOB_MAP_REQUEST -> {
                intent.removeExtra("type")
                val id = viewModel.getActiveJobId().value
                if (null == id || id == 0) {
                    ViewModelProviders.of(this)
                        .get(ScheduledJobListViewModel::class.java)
                        .getActiveJobId()
                        .observe(this, Observer {
                            viewModel.setActiveJobId(it)
                        })
                    binding.navigation.selectedItemId = R.id.job_map
                    viewModel.setPickupLocation(intent.getDoubleArrayExtra("pickup").let { LatLng(it[0], it[1]) })
                    viewModel.updateDropOffLocation(intent.getDoubleArrayExtra("dropoff").let {
                        LatLng(it[0], it[1])
                    })
                }
            }
        }
    }
}
