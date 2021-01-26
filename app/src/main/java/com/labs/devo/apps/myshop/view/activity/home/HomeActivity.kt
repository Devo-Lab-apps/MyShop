package com.labs.devo.apps.myshop.view.activity.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.databinding.ActivityMainBinding
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.activity.auth.AuthenticationActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: HomeViewModel by viewModels()

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    /**
     * Global coroutine scope
     */
    private lateinit var job: Job


    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()

        if (auth.currentUser != null && auth.currentUser!!.email != null) {
            viewModel.attachSnapshotToUser(auth.currentUser!!.email!!)
        }

        observeEvents()
    }

    private fun observeEvents() {
        job = GlobalScope.launch {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    HomeViewModel.HomeViewModelEvent.LogoutUser -> {
                        printLogD(TAG, "logging out")
                        auth.signOut()
                        val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        printLogD(TAG, "activity finishing")
                        finish()
                    }
                }
            }
        }
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
            R.id.accounts_nav -> openNotebookActivity()
            R.id.logout_user -> logoutUser()
        }
        return true
    }

    /**
     * Start accounts activity
     */
    private fun openNotebookActivity() {
        startActivity(Intent(this, NotebookActivity::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        printLogD(TAG, "On destroy")
        job.cancel()
    }
}