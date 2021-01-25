package com.labs.devo.apps.myshop

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings.Secure
import com.labs.devo.apps.myshop.util.AppData
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyShopApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        setDeviceId()
    }

    @SuppressLint("HardwareIds")
    private fun setDeviceId() {
        AppData.deviceId = Secure.getString(
            applicationContext.contentResolver,
            Secure.ANDROID_ID
        )
    }
}