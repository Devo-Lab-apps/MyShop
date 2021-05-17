package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.AppConstants.ONE_DAY_MILLIS
import com.labs.devo.apps.myshop.util.printLogD
import java.util.*

data class NotificationMetadataBuilder(
    val uniqueNotificationName: String,
    val channelId: String,
    val notificationId: String,
    val isRepeating: Boolean,
    val initialDelay: TimeDuration,
    val metadata: String
) {
    fun getEffectiveInitialDelay(): Pair<Long, Long> {
        val time = initialDelay.timeAtHours.split(":")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time[0].toInt())
        calendar.set(Calendar.MINUTE, time[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        if (calendar.before(Calendar.getInstance())) {
            val nextIteration = getNextIteration()
            calendar.add(nextIteration.first, nextIteration.second)
        }
        return Pair(calendar.timeInMillis, getNextIter())
    }

    private fun getNextIteration(): Pair<Int, Int> {
        return when (initialDelay.frequency) {
            "Daily" -> Pair(Calendar.DATE, 1)
            "Weekly" -> Pair(Calendar.WEEK_OF_YEAR, 1)
            "Biweekly" -> Pair(Calendar.WEEK_OF_YEAR, 2)
            "Monthly" -> Pair(Calendar.MONTH, 1)
            else -> Pair(Calendar.DATE, 1)
        }
    }

    private fun getNextIter(): Long {
        return when (initialDelay.frequency) {
            "Daily" -> ONE_DAY_MILLIS.toLong()
            "Weekly" -> ONE_DAY_MILLIS * 7L
            "Biweekly" -> ONE_DAY_MILLIS * 14L
            "Monthly" -> ONE_DAY_MILLIS * 28L
            else -> ONE_DAY_MILLIS.toLong()
        }
    }

}

data class TimeDuration(
    val timeAtHours: String,
    val frequency: String
)