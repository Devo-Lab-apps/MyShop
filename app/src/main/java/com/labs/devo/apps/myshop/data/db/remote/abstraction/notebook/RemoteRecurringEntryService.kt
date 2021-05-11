package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

interface RemoteRecurringEntryService {

    suspend fun getRecurringEntries(
        pageId: String?,
        startAfter: String
    ): List<RecurringEntry>

    suspend fun insertRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry

    suspend fun updateRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry

    suspend fun deleteRecurringEntry(recurringEntry: RecurringEntry)
}