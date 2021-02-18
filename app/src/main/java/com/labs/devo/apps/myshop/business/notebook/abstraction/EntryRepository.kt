package com.labs.devo.apps.myshop.business.notebook.abstraction

import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface EntryRepository {

    suspend fun getEntries(pageId: String, searchQuery: String): Flow<DataState<List<Entry>>>

    suspend fun insertEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun insertEntry(entry: Entry): DataState<Entry>

    suspend fun updateEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun updateEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntry(entry: Entry): DataState<Entry>

    suspend fun deleteEntries(entries: List<Entry>): DataState<List<Entry>>

    suspend fun syncEntries(pageId: String): DataState<List<Entry>>
}