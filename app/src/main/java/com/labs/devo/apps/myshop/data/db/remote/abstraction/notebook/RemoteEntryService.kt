package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Entry

interface RemoteEntryService {

    suspend fun getEntries(pageId: String, query: String, startAfter: String?): List<Entry>

    suspend fun insertEntries(entries: List<Entry>): List<Entry>

    suspend fun insertEntry(entry: Entry): Entry

    suspend fun updateEntries(entries: List<Entry>): List<Entry>

    suspend fun updateEntry(entry: Entry): Entry

    suspend fun deleteEntry(entry: Entry)

    suspend fun deleteEntries(entries: List<Entry>)

}