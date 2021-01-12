package com.labs.devo.apps.myshop.view.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.databinding.ActivitySplashBinding
import com.labs.devo.apps.myshop.helper.PreferencesManager
import com.labs.devo.apps.myshop.view.activity.auth.AuthenticationActivity
import com.labs.devo.apps.myshop.view.activity.intro.IntroActivity
import com.labs.devo.apps.myshop.view.activity.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: PreferencesManager

    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    @Suppress("DEPRECATION")
    private fun init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        // hide the action bar
        supportActionBar?.hide()
        val anim = AnimationUtils.loadAnimation(this, R.anim.splash_text)
        binding.splashTxt.animation = anim
        Handler(Looper.getMainLooper()).postDelayed({
            if (isIntroShown()) {
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                finish()
            }
        }, AppConstants.SPLASH_TIME.toLong())
    }

    private fun isIntroShown(): Boolean {
        //TODO find a way out and use this wisely.
        val isIntoShown: Boolean
        runBlocking {
            isIntoShown = preferences.introActivityShown.first()
        }
        return isIntoShown
    }
}