package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.notebook.Entry

interface LocalEntryService {

    fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Entry>

    suspend fun getEntry(entryId: String): Entry?

    suspend fun insertEntries(entries: List<Entry>)

    suspend fun insertEntry(entry: Entry)

    suspend fun updateEntries(entries: List<Entry>)

    suspend fun updateEntry(entry: Entry)

    suspend fun deleteEntry(entry: Entry)

    suspend fun deleteEntries(entries: List<Entry>)

    suspend fun deleteEntries(pageId: String)

}