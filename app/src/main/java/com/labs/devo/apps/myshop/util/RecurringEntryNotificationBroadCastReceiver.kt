package com.labs.devo.apps.myshop.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager.NOTIFICATION_ID
import com.labs.devo.apps.myshop.business.helper.NotificationBuilder
import com.labs.devo.apps.myshop.business.helper.NotificationMetadataBuilder
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class RecurringEntryNotificationBroadCastReceiver : BroadcastReceiver() {
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
        val recurringEntry: RecurringEntry = MyNotificationManager.gson.fromJson(
            notificationMetadataBuilder.metadata,
            RecurringEntry::class.java
        )
        sendNotification(context, notificationBuilder, notificationMetadataBuilder, recurringEntry)
        if (notificationMetadataBuilder.isRepeating) {
            //TODO add delay to prevent two notification at same time if alarm gets triggered before the time and registers again.
            CoroutineScope(IO).launch {
                delay(1000)
                MyNotificationManager.registerRecurringEntryWork(context, recurringEntry, true)
            }
        }
    }

    private fun sendNotification(
        context: Context,
        notificationBuilder: NotificationBuilder,
        notificationMetadataBuilder: NotificationMetadataBuilder,
        recurringEntry: RecurringEntry
    ) {
        val actions =
            getActions(context, recurringEntry, notificationMetadataBuilder.notificationId)
        val builder =
            notificationBuilder.getNotification(context, notificationMetadataBuilder.channelId)
        actions.forEach {
            builder.addAction(it)
        }
        notificationBuilder.sendNotification(
            context,
            notificationMetadataBuilder.channelId,
            notificationMetadataBuilder.notificationId,
            builder
        )
    }

    private fun getActions(
        context: Context,
        recurringEntry: RecurringEntry,
        notificationId: String
    ): List<NotificationCompat.Action> {
        val i = Intent(context, RecurringEntryNotificationActionReceiver::class.java)
        i.putExtra(RECURRING_ENTRY, recurringEntry)
        i.action = "com.labs.devo.apps.myshop.RECURRING_ENTRY_ACTION"
        //multiplied by 2 for getting id associated with this notification
        val notifId = notificationId.hashCode() * 2
        i.putExtra(NOTIFICATION_ID, notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notifId,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action =
            NotificationCompat.Action(R.mipmap.ic_launcher_round, "Create Entry", pendingIntent)
        return listOf(action)
    }
}