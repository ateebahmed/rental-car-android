package com.rent24.driver.components.login

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rent24.driver.R
import com.rent24.driver.api.login.response.LoginResponse
import com.rent24.driver.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var model: LoginViewModel

    private val TAG = LoginActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Starting LoginActivity.onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        model = ViewModelProviders.of(this)
            .get(LoginViewModel::class.java)

        binding.model = model
        setupModel(model)

        setupActionBar()
    }

    private fun setupModel(model: LoginViewModel) {
        Log.d(TAG, "Starting LoginActivity.setupModel")
        model.getEmail().observe(this, Observer<String> { newEmail ->
            binding.email
                .setText(newEmail)
        })

        model.getPassword().observe(this, Observer<String> { newPassword ->
            binding.password
                .setText(newPassword)
        })

        binding.emailSignInButton
            .setOnClickListener {
                model.callLoginApi(binding.email
                    .text
                    .toString(),
                    binding.password
                        .text
                        .toString(), "karachipakistan")
            }

        model.getToken()
            .observe(this, Observer<LoginResponse> {
                if (it?.error
                        ?.error
                        .isNullOrEmpty()) {
                    Snackbar.make(binding.container, "Failed", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Snackbar.make(binding.container, "Success", Snackbar.LENGTH_SHORT)
                        .show()
                }
            })
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
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
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
