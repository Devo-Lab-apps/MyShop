package com.labs.devo.apps.myshop.view.activity.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.ActivityIntroBinding
import com.labs.devo.apps.myshop.helper.PreferencesManager
import com.labs.devo.apps.myshop.view.activity.auth.AuthenticationActivity
import com.labs.devo.apps.myshop.view.adapter.intro.IntroViewPageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: PreferencesManager

    private val screenItems: ArrayList<IntroScreenItem> = ArrayList()

    private lateinit var binding: ActivityIntroBinding
    private lateinit var btnAnim: Animation
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        fillData()
        setupTabs()

    }

    @Suppress("DEPRECATION")
    private fun init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        // hide the action bar
        supportActionBar?.hide()
        btnAnim =
            AnimationUtils.loadAnimation(this@IntroActivity, R.anim.get_started_btn_anim)
    }

    private fun setupTabs() {

        val introViewPagerAdapter = IntroViewPageAdapter(this, screenItems)
        binding.screenViewpager.adapter = introViewPagerAdapter

        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)

        binding.btnNext.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < screenItems.size) {
                unloadLastScreen()
                binding.screenViewpager.currentItem = position + 1
            }

            if (position + 1 == screenItems.size - 1) { // when we reach to the last screen
                loadLastScreen()
            }
        }

        // tab layout add change listener


        binding.tabIndicator.addOnTabSelectedListener(
            object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (tab.position < screenItems.size - 1) {
                        unloadLastScreen()
                    }
                    if (tab.position == screenItems.size - 1) {
                        loadLastScreen()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            }
        )


        // Get Started button click listener
        binding.btnGetStarted.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                setIsIntroShown()
            }

            //open main activity
            val mainActivity = Intent(this@IntroActivity, AuthenticationActivity::class.java)
            startActivity(mainActivity)
            finish()
        }

        //skip button click listener
        binding.tvSkip.setOnClickListener {
            binding.screenViewpager.currentItem = screenItems.size
        }

    }


    private fun fillData() {
        screenItems.add(
            IntroScreenItem(
                "Fresh Food",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.intro_img1
            )
        )
        screenItems.add(
            IntroScreenItem(
                "Fast Delivery",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.intro_img2
            )
        )
        screenItems.add(
            IntroScreenItem(
                "Easy Payment",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",
                R.drawable.intro_img3
            )
        )
    }

    private suspend fun setIsIntroShown() {
        preferences.updateIntoActivityShown(true)
    }

    // show the GET STARTED Button and hide the indicator and the next button
    fun loadLastScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnGetStarted.visibility = View.VISIBLE
        binding.tvSkip.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE
        binding.btnGetStarted.animation = btnAnim
    }

    private fun unloadLastScreen() {
        binding.btnGetStarted.visibility = View.INVISIBLE
        binding.btnNext.visibility = View.VISIBLE
        binding.tvSkip.visibility = View.VISIBLE
        binding.tabIndicator.visibility = View.VISIBLE
    }
}