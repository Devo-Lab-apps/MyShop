package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Entry

interface RemoteEntryService {

    suspend fun getEntries(pageId: String, query: String, startAfter: String?): List<Entry>

    suspend fun getEntry(pageId: String, entryId: String): Entry

    suspend fun createEntry(entry: Entry): Entry

    suspend fun updateEntry(entry: Entry): Entry

    suspend fun deleteEntry(entry: Entry)

}