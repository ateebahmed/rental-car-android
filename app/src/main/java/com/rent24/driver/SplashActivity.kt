package com.rent24.driver

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.rent24.driver.components.home.HomeActivity
import com.rent24.driver.components.login.LoginActivity
import com.rent24.driver.databinding.ActivitySplashBinding

private const val LOGIN_REQUEST_CODE = 200

private val TAG = SplashActivity::class.java.name

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(SplashViewModel::class.java)
    }
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.progressCircular.show()
        binding.lifecycleOwner = this
        setModelObservers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.onActivityResult(requestCode, resultCode, LOGIN_REQUEST_CODE)
    }

    private fun setModelObservers() {
        viewModel.getNetworkStatus()
            .observe(this, Observer {
                if (it) {
                    viewModel.checkSession()
                } else {
                    binding.progressCircular.hide()
                    Toast.makeText(applicationContext, "No network available!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
            })
        viewModel.getActivitySwitcher()
            .observe(this, Observer {
                binding.progressCircular.hide()
                if (it) {
                    startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_REQUEST_CODE)
                } else {
                    viewModel.updateFirebaseToken()
                    startHomeActivity()
                }
            })
        viewModel.getShowHomeActivity()
            .observe(this, Observer {
                binding.progressCircular.hide()
                if (it) {
                    viewModel.updateFirebaseToken()
                    startHomeActivity()
                } else {
                    finish()
                }
            })

        viewModel.checkNetwork(Runtime.getRuntime(),
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
