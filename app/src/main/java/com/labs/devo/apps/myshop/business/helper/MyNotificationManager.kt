package com.labs.devo.apps.myshop.business.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.labs.devo.apps.myshop.util.MyBroadCastReceiver


object MyNotificationManager {
    val gson: Gson = Gson()
    const val NOTIFICATION_DATA = "notification_data"
    const val ALARM_METADATA = "alarm_metadata"
    private const val CHANNEL_ID = "channel_id"
    private const val NOTIFICATION_ID = "notification_id"

    // private const val RESULT_ID = "id"

    fun sendSingleNotification(
        context: Context,
        notificationMetadataBuilder: NotificationMetadataBuilder,
        notificationBuilder: NotificationBuilder
    ) {
        val manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, MyBroadCastReceiver::class.java)
        alarmIntent.putExtra(NOTIFICATION_DATA, gson.toJson(notificationBuilder))
        alarmIntent.putExtra(
            ALARM_METADATA,
            gson.toJson(notificationMetadataBuilder)
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationMetadataBuilder.uniqueNotificationName.hashCode(),
            alarmIntent,
            0
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

    fun sendRecurringNotification(
        context: Context,
        notificationMetadataBuilder: NotificationMetadataBuilder,
        notificationBuilder: NotificationBuilder
    ) {
        val manager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, MyBroadCastReceiver::class.java)
        alarmIntent.putExtra(NOTIFICATION_DATA, gson.toJson(notificationBuilder))
        alarmIntent.putExtra(
            ALARM_METADATA,
            gson.toJson(notificationMetadataBuilder)
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationMetadataBuilder.uniqueNotificationName.hashCode(),
            alarmIntent,
            0
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

    fun checkIfAlarmExists(context: Context, notificationUniqueName: String): Boolean {
        val alarmIntent = Intent(context, MyBroadCastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, notificationUniqueName.hashCode(), alarmIntent, 0)
        return pendingIntent != null
    }
}