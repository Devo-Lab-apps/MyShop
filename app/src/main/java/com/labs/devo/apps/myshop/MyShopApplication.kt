package com.labs.devo.apps.myshop

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.provider.Settings.Secure
import com.labs.devo.apps.myshop.util.AppData
import dagger.hilt.android.HiltAndroidApp

const val RECURRING_ENTRY_CHANNEL = "recurring_entry_channel"
const val CHANNEL_2_ID = "channel2"

@HiltAndroidApp
class MyShopApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        setDeviceId()
        createNotificationChannels()
    }

    @SuppressLint("HardwareIds")
    private fun setDeviceId() {
        AppData.deviceId = Secure.getString(
            applicationContext.contentResolver,
            Secure.ANDROID_ID
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                RECURRING_ENTRY_CHANNEL,
                "Recurring Entries",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Channel to notify recurring entries"
            val channel2 = NotificationChannel(
                CHANNEL_2_ID,
                "Channel 2",
                NotificationManager.IMPORTANCE_LOW
            )
            channel2.description = "This is Channel 2"
            val manager = getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager
            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
        }
    }
}