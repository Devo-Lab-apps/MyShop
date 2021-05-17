package com.labs.devo.apps.myshop.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager
import com.labs.devo.apps.myshop.business.helper.NotificationBuilder
import com.labs.devo.apps.myshop.business.helper.NotificationMetadataBuilder
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NotificationBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
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
        val recurringEntry = MyNotificationManager.gson.fromJson(
            notificationMetadataBuilder.metadata,
            RecurringEntry::class.java
        )
        if (notificationMetadataBuilder.isRepeating) {
            //TODO add delay to prevent two notification at same time if alarm gets triggered before the time and registers again.
            CoroutineScope(IO).launch {
                delay(1000)
                MyNotificationManager.registerWork(context, recurringEntry, true)
            }
        }
    }
}