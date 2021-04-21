package com.labs.devo.apps.myshop.data.repo.notebook.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface EntryRepository {

    suspend fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean,
        isRepeating: Boolean
    ): Flow<PagingData<Entry>>

    suspend fun getEntry(entryId: String): DataState<Entry>

    suspend fun insertEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun insertEntry(entry: Entry): DataState<Entry>

    suspend fun updateEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun updateEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun deleteEntries()

    suspend fun syncEntries(pageId: String): DataState<List<Entry>>
}