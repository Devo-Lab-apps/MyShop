package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.QueryParams

interface LocalEntryService {

    suspend fun getEntries(pageId: String, queryParams: QueryParams): List<Entry>?

    suspend fun insertEntries(entries: List<Entry>): List<Entry>

    suspend fun insertEntry(entry: Entry): Entry

    suspend fun updateEntries(entries: List<Entry>): List<Entry>

    suspend fun updateEntry(entry: Entry): Entry

    suspend fun deleteEntry(entry: Entry)

    suspend fun deleteEntries(entries: List<Entry>)

    suspend fun deleteEntries(pageId: String)

}