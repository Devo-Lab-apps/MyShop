package com.labs.devo.apps.myshop.view.util

import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.labs.devo.apps.myshop.business.helper.NotificationBuilder
import com.labs.devo.apps.myshop.business.helper.NotificationWorkManagerBuilder
import com.labs.devo.apps.myshop.const.AppConstants.TAG
import com.labs.devo.apps.myshop.util.printLogD
import java.util.*


class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        val gson: Gson = Gson()
        private const val NOTIFICATION_WORK_DATA = "notification_work_data"
        private const val NOTIFICATION_WORK_MANAGER = "notification_work_manager"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = "notification_id"

        // private const val RESULT_ID = "id"

        fun sendSingleNotification(
            context: Context,
            notificationWorkManagerBuilder: NotificationWorkManagerBuilder,
            notificationBuilder: NotificationBuilder
        ) {
            val workManager = WorkManager.getInstance(context)
            val data = workDataOf(
                NOTIFICATION_WORK_DATA to gson.toJson(notificationBuilder),
                NOTIFICATION_WORK_MANAGER to gson.toJson(notificationWorkManagerBuilder)
            )
            val initialDelay = notificationWorkManagerBuilder.getEffectiveInitialDelay()
            val builder =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(initialDelay.first, initialDelay.second)
                    .setInputData(data)
            val workRequest = builder.build()
            workManager.enqueueUniqueWork(
                notificationWorkManagerBuilder.uniqueWorkName,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelNotification(context: Context, uniqueWorkName: String): Operation {
            val workManager = WorkManager.getInstance(context)
            return workManager.cancelUniqueWork(uniqueWorkName)
        }

        fun cancelAllWork(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
        }

        fun checkIfWorkExists(context: Context, workId: String): Boolean {
            val workManager = WorkManager.getInstance(context)
            val work = workManager.getWorkInfosForUniqueWork(workId).get()
            return work.any { !it.state.isFinished }
        }
    }


    override suspend fun doWork(): Result {
        val context = applicationContext
        val workManagerBuilder = inputData.getString(NOTIFICATION_WORK_MANAGER)
        val notificationBuilderJSON = inputData.getString(NOTIFICATION_WORK_DATA)
        printLogD(TAG, workManagerBuilder, notificationBuilderJSON)
        val notificationBuilder =
            gson.fromJson(notificationBuilderJSON, NotificationBuilder::class.java)
        val notificationWorkManagerBuilder =
            gson.fromJson(workManagerBuilder, NotificationWorkManagerBuilder::class.java)

        notificationBuilder.sendNotification(
            context,
            notificationWorkManagerBuilder.channelId,
            notificationWorkManagerBuilder.notificationId
        )
        if (notificationWorkManagerBuilder.isRepeating) {
            sendSingleNotification(context, notificationWorkManagerBuilder, notificationBuilder)
        }
        return Result.success()
    }
}