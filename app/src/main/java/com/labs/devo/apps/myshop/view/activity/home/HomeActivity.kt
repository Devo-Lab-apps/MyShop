package com.labs.devo.apps.myshop.view.activity.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.ActivityMainBinding
import com.labs.devo.apps.myshop.view.activity.auth.AuthenticationActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
    }

    /**
     * Initialise the view in the activity.
     */
    private fun initView() {
        setSupportActionBar(binding.homeToolbar)
        val actionDrawable = ActionBarDrawerToggle(
            this,
            binding.homeDrawerLayout,
            binding.homeToolbar,
            R.string.open_drawer_description,
            R.string.close_drawer_description,
        )

        binding.homeDrawerLayout.addDrawerListener(actionDrawable)
        actionDrawable.syncState()
        binding.homeNav.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.accounts_nav -> openAccountsActivity()
            R.id.logout_user -> logoutUser()
        }
        return true
    }

    /**
     * Start accounts activity
     */
    private fun openAccountsActivity() {
    }

    /**
     * Method to logout the user.
     */
    private fun logoutUser() {
        binding.homeProgressBar.visibility = View.VISIBLE

        auth.signOut()
        startActivity(Intent(this@HomeActivity, AuthenticationActivity::class.java))
        finish()
    }
}