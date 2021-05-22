package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

interface LocalRecurringEntryService {
    fun getRecurringEntries(
        pageId: String
    ): PagingSource<Int, RecurringEntry>

    suspend fun getRecurringEntry(recurringEntryId: String): RecurringEntry?

    suspend fun createRecurringEntries(entries: List<RecurringEntry>)

    suspend fun createRecurringEntry(entry: RecurringEntry)

    suspend fun updateRecurringEntry(entry: RecurringEntry)

    suspend fun deleteRecurringEntry(entry: RecurringEntry)

    suspend fun deleteRecurringEntries()

    suspend fun deleteRecurringEntries(pageId: String)

    suspend fun getLastFetchedRecurringEntry(pageId: String): RecurringEntry?
}