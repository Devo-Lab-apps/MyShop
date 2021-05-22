package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import kotlinx.coroutines.flow.Flow

interface LocalEntryService {

    fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry>

    fun getEntriesLikeEntryId(
        entryId: String,
        dateRange: Pair<Long, Long>,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry>

    fun getEntriesTotalAmount(pageId: String): LiveData<Double>

    suspend fun getEntry(entryId: String): Entry?

    suspend fun createEntries(entries: List<Entry>)

    suspend fun createEntry(entry: Entry)

    suspend fun updateEntry(entry: Entry)

    suspend fun deleteEntry(entry: Entry)

    suspend fun deleteEntriesLikeEntryId(entryId: String)

    suspend fun deleteEntries(pageId: String)

    suspend fun deleteEntries()

}