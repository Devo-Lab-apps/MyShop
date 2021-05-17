package com.labs.devo.apps.myshop.business.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.labs.devo.apps.myshop.RECURRING_ENTRY_CHANNEL
import com.labs.devo.apps.myshop.const.AppConstants.TAG
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.util.NotificationBroadCastReceiver
import com.labs.devo.apps.myshop.util.printLogD


object MyNotificationManager {
    val gson: Gson = Gson()
    const val NOTIFICATION_DATA = "notification_data"
    const val ALARM_METADATA = "alarm_metadata"
    private const val CHANNEL_ID = "channel_id"
    private const val NOTIFICATION_ID = "notification_id"

    // private const val RESULT_ID = "id"

    private fun sendSingleNotification(
        context: Context,
        notificationMetadataBuilder: NotificationMetadataBuilder,
        notificationBuilder: NotificationBuilder
    ) {
        val manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotificationBroadCastReceiver::class.java)
        alarmIntent.putExtra(NOTIFICATION_DATA, gson.toJson(notificationBuilder))
        alarmIntent.putExtra(
            ALARM_METADATA,
            gson.toJson(notificationMetadataBuilder)
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationMetadataBuilder.uniqueNotificationName.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val initialDelay = notificationMetadataBuilder.getEffectiveInitialDelay()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                initialDelay.first,
                pendingIntent
            )
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent)
        }
    }

    private fun sendRecurringNotification(
        context: Context,
        notificationMetadataBuilder: NotificationMetadataBuilder,
        notificationBuilder: NotificationBuilder
    ) {
        val manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotificationBroadCastReceiver::class.java)
        alarmIntent.putExtra(NOTIFICATION_DATA, gson.toJson(notificationBuilder))
        alarmIntent.putExtra(
            ALARM_METADATA,
            gson.toJson(notificationMetadataBuilder)
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationMetadataBuilder.uniqueNotificationName.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val initialDelay = notificationMetadataBuilder.getEffectiveInitialDelay()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                initialDelay.first,
                initialDelay.second,
                pendingIntent
            )
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        }
    }

    private fun checkIfAlarmExists(context: Context, notificationUniqueName: String): Boolean {
        val alarmIntent = Intent(context, NotificationBroadCastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationUniqueName.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_NO_CREATE
            )
        return pendingIntent != null
    }

    fun cancelIfAlarmExists(context: Context, notificationUniqueName: String) {
        val manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationBroadCastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationUniqueName.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_NO_CREATE
            )
        if (pendingIntent != null) {
            printLogD(TAG, "Cancelling alarm with id: $notificationUniqueName")
            manager.cancel(pendingIntent)
            pendingIntent.cancel()
        } else {
            printLogD(TAG, "Can't cancel, alarm doesn't exist with id: $notificationUniqueName")
        }
    }


    fun registerWork(
        context: Context,
        recurringEntry: RecurringEntry,
        registerOverride: Boolean = false
    ) {
        if (registerOverride || !checkIfAlarmExists(
                context,
                recurringEntry.recurringEntryId
            )
        ) {
            printLogD(TAG, "Registering: ${recurringEntry.recurringEntryId}")
            val re = gson.toJson(recurringEntry)
            sendSingleNotification(
                context,
                NotificationMetadataBuilder(
                    recurringEntry.recurringEntryId,
                    RECURRING_ENTRY_CHANNEL,
                    recurringEntry.recurringEntryId,
                    true,
                    TimeDuration(recurringEntry.recurringTime, recurringEntry.frequency),
                    re
                ),
                NotificationBuilder(
                    recurringEntry.name,
                    "Add ${recurringEntry.amount}?"
                ),
            )
        } else {
            printLogD(TAG, "Already registered: ${recurringEntry.recurringEntryId}")
        }
    }


}