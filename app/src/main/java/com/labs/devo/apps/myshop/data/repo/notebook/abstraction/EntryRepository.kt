package com.labs.devo.apps.myshop.data.repo.notebook.abstraction

import androidx.lifecycle.LiveData
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

    suspend fun getEntry(pageId: String, entryId: String): DataState<Entry>

    suspend fun createEntry(entry: Entry): DataState<Entry>

    suspend fun updateEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntries()

    suspend fun syncEntries(pageId: String): DataState<List<Entry>>

    fun getEntriesTotalAmount(pageId: String): LiveData<Double>
}