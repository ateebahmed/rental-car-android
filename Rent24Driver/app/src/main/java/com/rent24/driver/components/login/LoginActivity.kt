package com.rent24.driver.components.login

import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = LoginActivity::class.java.name
    private val signInButtonClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            if (binding.email
                    .text
                    .isNotEmpty() && binding.password
                    .text.isNotEmpty()
            ) {
                binding.loginProgress
                    .show()
                binding.model?.callLoginApi(
                    binding.email
                        .text
                        .toString(), binding.password
                        .text
                        .toString()
                )
            } else {
                Snackbar.make(binding.container, "One or more inputs are empty", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private val onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener by lazy {
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            binding.loginProgress
                .hide()
            key ?: return@OnSharedPreferenceChangeListener
            when (key) {
                "token" -> {
                    Log.i(TAG, "login successful")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                "error" -> Snackbar.make(binding.container, sharedPreferences?.getString(key,
                    "Error occured while processing")!!, Snackbar.LENGTH_SHORT)
                    .show()
                else -> Snackbar.make(binding.container, "Wrong credentials, Try again", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Starting LoginActivity.onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        binding.model = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(application))
            .get(LoginViewModel::class.java)
        setupModel(binding.model!!)
        setupActionBar()
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    override fun onBackPressed() {
        if (binding.loginProgress
                .isShown) {
            binding.loginProgress
                .hide()
        }
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        super.onStop()
    }

    private fun setupModel(model: LoginViewModel) {
        Log.d(TAG, "Starting LoginActivity.setupModel")
        model.getEmail()
            .observe(this, Observer<String> { newEmail ->
                binding.email
                    .setText(newEmail)
            })

        model.getPassword()
            .observe(this, Observer<String> { newPassword ->
                binding.password
                    .setText(newPassword)
            })

        binding.emailSignInButton
            .setOnClickListener(signInButtonClickListener)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok)
                { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) }
        } else {
            requestPermissions(arrayOf(READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
        return false
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0
    }
}
