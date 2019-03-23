package com.rent24.driver.components.login

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.databinding.ActivityLoginBinding

private val TAG = LoginActivity::class.java.name

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Starting LoginActivity.onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        binding.model = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(application))
            .get(LoginViewModel::class.java)
        setupModelObservers(binding.model!!)
        setupActionBar()
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .registerOnSharedPreferenceChangeListener(binding.model
                ?.onSharedPreferenceChangeListener)
    }

    override fun onBackPressed() {
        binding.model
            ?.onActivityBackPressed()
        super.onBackPressed()
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .unregisterOnSharedPreferenceChangeListener(binding.model
                ?.onSharedPreferenceChangeListener)
        super.onStop()
    }

    private fun setupModelObservers(model: LoginViewModel) {
        Log.d(TAG, "Starting LoginActivity.setupModelObservers")

        binding.email
            .onFocusChangeListener = model.onEmailFocusChangeListener
        binding.password
            .onFocusChangeListener = model.onPasswordFocusChangeListener
        binding.emailSignInButton
            .setOnClickListener(model.signInButtonClickListener)
        binding.model
            ?.shouldShowLoadingProgressBar()!!
            .observe(this, Observer {
                if (it && !binding.loginProgress
                        .isShown) {
                    binding.loginProgress
                        .show()
                } else if (binding.loginProgress
                        .isShown) {
                    binding.loginProgress
                        .hide()
                }
            })
        binding.model
            ?.getSnackbarMessage()!!
            .observe(this, Observer {
                Snackbar.make(binding.container, it, Snackbar.LENGTH_SHORT)
                    .show()
            })
        binding.model
            ?.getActivityResult()!!
            .observe(this, Observer {
                setResult(it)
                finish()
            })
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
