package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.const.AppConstants.ONE_DAY_MILLIS
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.database.dao.RateDao
import com.labs.devo.apps.myshop.util.exceptions.OperationLimitExceedException
import com.labs.devo.apps.myshop.util.exceptions.TimeChangedException
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class RateLimiter @Inject constructor(private val rateDao: RateDao) {

    suspend fun checkRateLimit(operationName: Permissions) {
        val rateLimit = rateDao.getLimit(operationName.name)
        val operation = allowedOperationMap[operationName.name]!!
        var timestamps = rateLimit.timestamps
        if (timestamps.any { it < System.currentTimeMillis() }) {
            throw TimeChangedException()
        }
        if (timestamps.size > 0) {
            val firstOperation = timestamps[0]
            val currentTime = System.currentTimeMillis()
            if (currentTime - firstOperation < operation.timeBlocked) {
                if (timestamps.size + 1 > operation.limit) {
                    throw OperationLimitExceedException(operationName.name)
                }
            }
            var i = 0
            while (i < timestamps.size && currentTime - operation.timeBlocked > timestamps[i]) i++
            timestamps =
                timestamps.subList(i, timestamps.size - 1)

        }
        AsyncHelper.runAsyncInBackground {
            rateLimit.timestamps = timestamps
            rateLimit.count += 1
            timestamps.add(System.currentTimeMillis())
            rateDao.update(rateLimit)
        }
    }


}

//TODO add appropriate keys
val allowedOperationMap = mapOf<String, LimitTimeKey>()

data class LimitTimeKey(val limit: Int, val timeBlocked: Long)