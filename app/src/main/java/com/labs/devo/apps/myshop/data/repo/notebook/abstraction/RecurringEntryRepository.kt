package com.labs.devo.apps.myshop.data.repo.notebook.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface RecurringEntryRepository {

    suspend fun getRecurringEntries(
        pageId: String?,
        forceRefresh: Boolean
    ): Flow<PagingData<RecurringEntry>>

    suspend fun getRecurringEntry(recurringEntryId: String): DataState<RecurringEntry>

    suspend fun createRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry>

    suspend fun updateRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry>

    suspend fun deleteRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry>

    suspend fun deleteRecurringEntries()

    suspend fun syncRecurringEntries(pageId: String): DataState<List<RecurringEntry>>
}