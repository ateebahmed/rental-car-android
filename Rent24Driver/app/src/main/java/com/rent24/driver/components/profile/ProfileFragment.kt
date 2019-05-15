package com.rent24.driver.components.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.rent24.driver.R
import com.rent24.driver.SplashActivity
import com.rent24.driver.components.login.LoginActivity
import com.rent24.driver.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.profile_fragment, container, false)
        binding = ProfileFragmentBinding.bind(layout)
        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this
        binding.model = ViewModelProviders.of(activity!!)
            .get(ProfileViewModel::class.java)
        binding.model?.getProfile()
            ?.observe(this, Observer {
                binding.email.text = it.email
                binding.username.text = it.name
                binding.mobile.text = it.phone
            })
        binding.logout.setOnClickListener {
            with(PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
                .edit()) {
                remove("token")
                apply()
            }
            startActivity(Intent(activity!!, SplashActivity::class.java))
            activity!!.finish()
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
