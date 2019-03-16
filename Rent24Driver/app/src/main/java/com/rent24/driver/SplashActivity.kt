package com.rent24.driver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rent24.driver.components.HomeActivity
import com.rent24.driver.components.login.LoginActivity

const val LOGIN_REQUEST_CODE = 200

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

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
        val token = getSharedPreferences("session", Context.MODE_PRIVATE).getString("token", "")
        if (token?.isEmpty()!!) {
            startActivityForResult(Intent(applicationContext, LoginActivity::class.java), LOGIN_REQUEST_CODE)
        } else {
            startHomeActivity()
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }
}
