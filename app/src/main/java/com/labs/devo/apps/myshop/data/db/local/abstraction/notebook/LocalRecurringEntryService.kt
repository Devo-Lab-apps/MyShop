package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

interface LocalRecurringEntryService {
    suspend fun getRecurringEntries(
        pageId: String
    ): List<RecurringEntry>

    suspend fun getRecurringEntry(recurringEntryId: String): RecurringEntry?

    suspend fun insertRecurringEntries(entries: List<RecurringEntry>)

    suspend fun insertRecurringEntry(entry: RecurringEntry)

    suspend fun updateRecurringEntries(entries: List<RecurringEntry>)

    suspend fun updateRecurringEntry(entry: RecurringEntry)

    suspend fun deleteRecurringEntry(entry: RecurringEntry)

    suspend fun deleteRecurringEntries(entries: List<RecurringEntry>)

    suspend fun deleteRecurringEntries()

    suspend fun deleteRecurringEntries(pageId: String)

    suspend fun getLastFetchedRecurringEntry(pageId: String): RecurringEntry?
}