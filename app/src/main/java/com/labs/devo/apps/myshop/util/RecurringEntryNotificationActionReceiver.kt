package com.labs.devo.apps.myshop.util

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.labs.devo.apps.myshop.business.helper.MyNotificationManager.NOTIFICATION_ID
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY
import com.labs.devo.apps.myshop.view.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@AndroidEntryPoint
class RecurringEntryNotificationActionReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var entryRepository: EntryRepository

    @Inject
    lateinit var userRepository: UserRepository


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent != null) {
            val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
                CoroutineScope(Main).launch {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            }
            val re = intent.getParcelableExtra<RecurringEntry>(RECURRING_ENTRY)!!
            val notificationId = intent.getStringExtra(NOTIFICATION_ID)

            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val entry = Entry(
                    re.pageId,
                    entryTitle = re.name,
                    entryDescription = "Created from recurring entry with id: ${re.recurringEntryId}",
                    entryAmount = re.amount
                )
                val dataState = entryRepository.createEntry(entry)
                handleCreateEntryDataState(context, dataState, notificationId)
            }
        }
    }

    private suspend fun handleCreateEntryDataState(
        context: Context?,
        dataState: DataState<Entry>,
        notificationId: String?
    ) {
        var msg = ""
        dataState.data?.let {
            msg = "Entry created"
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.cancel(notificationId, 0)
        } ?: run {
            msg = dataState.message?.getContentIfNotHandled() ?: UNKNOWN_ERROR_OCCURRED
        }
        withContext(Main) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
