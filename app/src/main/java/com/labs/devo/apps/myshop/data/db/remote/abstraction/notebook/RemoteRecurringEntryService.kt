package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

interface RemoteRecurringEntryService {

    suspend fun getRecurringEntries(
        pageId: String
    ): List<RecurringEntry>

    suspend fun insertRecurringEntries(recurringEntries: List<RecurringEntry>): List<RecurringEntry>

    suspend fun insertRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry

    suspend fun updateRecurringEntries(recurringEntries: List<RecurringEntry>): List<RecurringEntry>

    suspend fun updateRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry

    suspend fun deleteRecurringEntry(recurringEntry: RecurringEntry)

    suspend fun deleteRecurringEntries(recurringEntries: List<RecurringEntry>)
}