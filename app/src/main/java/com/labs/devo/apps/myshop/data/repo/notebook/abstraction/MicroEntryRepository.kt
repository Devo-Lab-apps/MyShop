package com.labs.devo.apps.myshop.data.repo.notebook.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow


interface MicroEntryRepository {

    suspend fun getMicroEntries(
        pageId: String,
        recurringEntry: RecurringEntry,
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Entry>>

//    suspend fun getEntry(entryId: String): DataState<Entry>

    suspend fun insertMicroEntries(recurringEntry: RecurringEntry, entries: List<Entry>): DataState<List<Entry>>

    suspend fun insertMicroEntry(recurringEntry: RecurringEntry, entry: Entry): DataState<Entry>

    suspend fun updateMicroEntries(recurringEntry: RecurringEntry, entries: List<Entry>): DataState<List<Entry>>

    suspend fun updateMicroEntry(recurringEntry: RecurringEntry, entry: Entry): DataState<Entry>

    suspend fun deleteMicroEntry(recurringEntry: RecurringEntry, entry: Entry): DataState<Entry>

    suspend fun deleteMicroEntries(recurringEntry: RecurringEntry, entries: List<Entry>): DataState<List<Entry>>
}