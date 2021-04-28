package com.labs.devo.apps.myshop.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager
import com.labs.devo.apps.myshop.business.helper.NotificationBuilder
import com.labs.devo.apps.myshop.business.helper.NotificationMetadataBuilder


class MyBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm Manager just ran 1", Toast.LENGTH_LONG)
            .show()
        val notificationMetadataJson = intent.getStringExtra(MyNotificationManager.ALARM_METADATA)
        val notificationBuilderJSON = intent.getStringExtra(MyNotificationManager.NOTIFICATION_DATA)
        val notificationBuilder =
            MyNotificationManager.gson.fromJson(
                notificationBuilderJSON,
                NotificationBuilder::class.java
            )
        val notificationMetadataBuilder =
            MyNotificationManager.gson.fromJson(
                notificationMetadataJson,
                NotificationMetadataBuilder::class.java
            )
        notificationBuilder.sendNotification(
            context,
            notificationMetadataBuilder.channelId,
            notificationMetadataBuilder.notificationId
        )
    }
}