package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.const.AppConstants.TAG
import com.labs.devo.apps.myshop.util.printLogD
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class NotificationWorkManagerBuilder(
    val uniqueWorkName: String,
    val channelId: String,
    val notificationId: String,
    val isRepeating: Boolean,
    private val initialDelay: TimeDuration
) {
    fun getEffectiveInitialDelay(): Pair<Long, TimeUnit> {
        val todayDateString =
            SimpleDateFormat("dd/MM/yyyy ", Locale.ENGLISH).format(Date(System.currentTimeMillis()))
        var todayReminderInSecondsFromEpoch = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.ENGLISH
        ).parse(todayDateString + initialDelay.timeAtHours)!!.time / 1000
        if (todayReminderInSecondsFromEpoch - (System.currentTimeMillis() / 1000) < 0) {
            todayReminderInSecondsFromEpoch += 86400 - 60
        }
        printLogD(TAG, todayReminderInSecondsFromEpoch - (System.currentTimeMillis() / 1000))
        val remainingTime = when (initialDelay.frequency) {
            "Daily" -> todayReminderInSecondsFromEpoch
            "Weekly" -> todayReminderInSecondsFromEpoch + (6 * 86400)
            "Biweekly" -> todayReminderInSecondsFromEpoch + (13 * 86400)
            "Monthly" -> todayReminderInSecondsFromEpoch + (27 * 86400)
             else -> todayReminderInSecondsFromEpoch
        }
        return Pair(remainingTime - (System.currentTimeMillis() / 1000), TimeUnit.SECONDS)
    }
}

data class TimeDuration(
    val timeAtHours: String,
    val frequency: String
)