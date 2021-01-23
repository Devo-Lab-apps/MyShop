package com.labs.devo.apps.myshop.view.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.ActivityAuthenticationBinding
import com.labs.devo.apps.myshop.view.activity.home.HomeActivity
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), DataStateListener {

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        supportActionBar?.hide()
    }

    /**
     * Handle fragment events.
     */
    override fun onDataStateChange(dataState: DataState<*>?){
        dataState?.let{
            // Handle loading
            showProgressBar(dataState.loading)

            // Handle Message
            dataState.message?.let{ event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar(isVisible: Boolean){
        binding.authProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {

    }
}