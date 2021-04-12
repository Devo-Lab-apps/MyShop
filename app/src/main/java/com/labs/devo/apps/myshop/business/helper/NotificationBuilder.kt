package com.labs.devo.apps.myshop.business.helper

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.labs.devo.apps.myshop.R


data class NotificationBuilder(
    val notificationTitle: String = "",
    val notificationContent: String = "",
    val notificationSmallIcon: Int = R.mipmap.ic_launcher_round,
    val notificationButtonText: String? = null,
    val notificationStyle: NotificationCompat.Style? = null,
    val notificationPendingIntent: (() -> PendingIntent)? = null,
    val notificationActions: List<NotificationCompat.Action>? = null,
    val notificationPriority: Int? = null,
    val notificationCategory: String? = null,
    val onGoing: Boolean = false,
    val alertOnlyOnce: Boolean = true,
    val autoCancel: Boolean = true
) {
    fun getNotification(context: Context, channelId: String): NotificationCompat.Builder {
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
        notificationBuilder.setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setOnlyAlertOnce(alertOnlyOnce)
            .setPriority(notificationPriority ?: NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(autoCancel)
            .setOngoing(onGoing)
            .setSmallIcon(notificationSmallIcon)
            .setCategory(notificationCategory ?: NotificationCompat.CATEGORY_REMINDER)
        notificationStyle?.let { style ->
            notificationBuilder.setStyle(style)
        }
        notificationPendingIntent?.let { pi ->
            notificationBuilder.setContentIntent(pi.invoke())
        }
        notificationActions?.let { actions ->
            if (actions.size > 3) {
                throw IllegalArgumentException("Actions must be less than or equal to 3")
            }
            actions.forEach { action ->
                notificationBuilder.addAction(action)
            }
        }
        return notificationBuilder
    }

    fun sendNotification(context: Context, channelId: String, notificationId: String) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, 0, getNotification(context, channelId).build())
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}