package com.rent24.driver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rent24.driver.components.HomeActivity
import com.rent24.driver.components.login.LoginActivity
import java.io.IOException

const val LOGIN_REQUEST_CODE = 200

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkSession()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            LOGIN_REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> startHomeActivity()
                    Activity.RESULT_CANCELED -> finish()
                }
            }
        }
    }

    private fun checkSession() {
        if (isNetworkAvailable() && canConnectToInternet()) {
            val token = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString("token", "")
            if (token?.isEmpty()!!) {
                startActivityForResult(Intent(applicationContext, LoginActivity::class.java), LOGIN_REQUEST_CODE)
            } else {
                startHomeActivity()
            }
        } else {
            Toast.makeText(applicationContext, "No network available!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }

    private fun isNetworkAvailable(): Boolean {
        val service = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info: NetworkInfo? = service.activeNetworkInfo
        return (null != info) && info.isConnectedOrConnecting
    }

    private fun canConnectToInternet(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val process = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            return process.waitFor() == 0
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message, e)
        }
        return false
    }
}
