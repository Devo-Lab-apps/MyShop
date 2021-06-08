package com.labs.devo.apps.myshop.view.activity.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager.registerRecurringEntryWork
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.dao.AlarmDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.AlarmKey
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.ActivityMainBinding
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.util.RecurringEntryNotificationBroadCastReceiver
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.activity.auth.AuthenticationActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.adapter.notebook.RecurringEntryListAdapter
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.util.*
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
    lateinit var alarmDao: AlarmDao

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var recurringAdapter: RecurringEntryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()

        if (auth.currentUser != null && auth.currentUser!!.email != null) {
            viewModel.attachSnapshotToUser(auth.currentUser!!.email!!)
        } else {
            logoutUser()
        }

        observeEvents()
    }

    private fun observeEvents() {
        job = GlobalScope.launch {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    HomeViewModel.HomeViewModelEvent.LogoutUser -> {
                        printLogD(TAG, "Logging out due to some reason")
                        showToast(
                            "You've been logged out of all devices."
                        )
                        logoutUser()
                    }
                    HomeViewModel.HomeViewModelEvent.UserNotFound -> {
                        printLogD(TAG, "User not found. Logging out.")
                        showToast("Someone deleted the user. Please retry later.")
                        logoutUser()
                    }
                    HomeViewModel.HomeViewModelEvent.DataCleared -> {
                        withContext(Dispatchers.Main) {
                            auth.signOut()
                            val intent =
                                Intent(this@HomeActivity, AuthenticationActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.entries.collectLatest { data ->
                recurringAdapter.submitData(data)
            }
        }

        lifecycleScope.launchWhenStarted {
            recurringAdapter.addLoadStateListener { state ->
                when (state.refresh) {
                    is LoadState.NotLoading -> {
                        recurringAdapter.snapshot().items.forEach { re ->
                            registerRecurringEntryWork(applicationContext, re)
                            AsyncHelper.runAsyncInBackground {
                                alarmDao.createOrReplace(
                                    AlarmKey(
                                        re.recurringEntryId,
                                        RecurringEntryNotificationBroadCastReceiver::class.qualifiedName
                                            ?: "com.labs.devo.apps.myshop.util.NotificationBroadCastReceiver"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun showToast(s: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@HomeActivity, s, Toast.LENGTH_LONG).show()
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
        recurringAdapter = RecurringEntryListAdapter(object :
            RecurringEntryListAdapter.OnRecurringEntryClick {
            override fun onClick(recurringEntry: RecurringEntry) {}
        })
        binding.homeRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = recurringAdapter
        }

        binding.homeDrawerLayout.addDrawerListener(actionDrawable)
        actionDrawable.syncState()
        binding.homeNav.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notebooks_nav -> openNotebookActivity()
            R.id.logout_user -> logoutUser()
            R.id.cancel_notification -> {

            }//printLogD(TAG, recurringAdapter.snapshot().items)
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
        binding.homeDrawerLayout.closeDrawers()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteAllLocalData()
            viewModel.dataCleared()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}