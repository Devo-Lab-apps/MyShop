package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.const.AppConstants
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
        val secondsInDay = 86400
        var todayReminderInSecondsFromEpoch = getReminderTime()

        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        //If the time is passed today then set it up for next day
        if (todayReminderInSecondsFromEpoch - (currentTimeInSeconds) < 0) {
            todayReminderInSecondsFromEpoch += secondsInDay - 10 // giving 10s buffer
        }
        printLogD(TAG, todayReminderInSecondsFromEpoch - (System.currentTimeMillis() / 1000))

        val remainingTime = getRemainingTime(todayReminderInSecondsFromEpoch)
        return Pair(remainingTime - (currentTimeInSeconds), TimeUnit.SECONDS)
    }

    private fun getRemainingTime(todayReminderInSecondsFromEpoch: Long): Long {
        val secondsInDay = 86400
        return when (initialDelay.frequency) {
            "Daily" -> todayReminderInSecondsFromEpoch
            "Weekly" -> todayReminderInSecondsFromEpoch + (6 * secondsInDay)
            "Biweekly" -> todayReminderInSecondsFromEpoch + (13 * secondsInDay)
            "Monthly" -> todayReminderInSecondsFromEpoch + (27 * secondsInDay)
            else -> todayReminderInSecondsFromEpoch
        }
    }

    private fun getReminderTime(): Long {
        val todayDateString =
            SimpleDateFormat(
                AppConstants.DATE_FORMAT,
                Locale.ENGLISH
            ).format(Date(System.currentTimeMillis()))
        return SimpleDateFormat(
            "${AppConstants.DATE_FORMAT} ${AppConstants.HOUR_FORMAT}",
            Locale.ENGLISH
        ).parse("$todayDateString ${initialDelay.timeAtHours}")!!.time / 1000
    }

}

data class TimeDuration(
    val timeAtHours: String,
    val frequency: String
)