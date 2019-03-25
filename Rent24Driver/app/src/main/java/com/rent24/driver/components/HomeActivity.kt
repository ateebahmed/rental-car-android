package com.rent24.driver.components

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.components.invoice.InvoiceFragment
import com.rent24.driver.components.job.JobFragment
import com.rent24.driver.components.job.list.JobListFragment
import com.rent24.driver.components.job.list.item.JobItemFragment
import com.rent24.driver.components.map.ParentMapFragment
import com.rent24.driver.components.profile.ProfileFragment
import com.rent24.driver.components.snaps.SnapsFragment
import com.rent24.driver.databinding.ActivityHomeBinding

private val TAG = HomeActivity::class.java.name

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var currentItem = 0
    private val onNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (currentItem == item.itemId) {
                return@OnNavigationItemSelectedListener false
            }
            currentItem = item.itemId
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
    private val viewModel by lazy {
        ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        binding.navigation
            .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupModel(viewModel)
        replaceFragment(onJobFragmentSelection())
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 &&
            supportFragmentManager.fragments
                .last() is JobItemFragment) {
            supportFragmentManager.popBackStack()
        } else super.onBackPressed()
    }

    private fun onMapFragmentSelection(): ParentMapFragment {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        return ParentMapFragment.newInstance()
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
                if (!it) {
                    switch.isChecked = false
                }
                if (!binding.apiProgress
                        .isShown) {
                    binding.apiProgress
                        .show()
                } else if (binding.apiProgress
                        .isShown) {
                    binding.apiProgress
                        .hide()
                }
            })
        model.getSnackbarMessage()
            .observe(this, Observer {
                Snackbar.make(binding.container, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
    }
}
