package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalRecurringEntryService
import com.labs.devo.apps.myshop.data.db.local.database.dao.RecurringEntryDao
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalRecurringEntryServiceImpl
@Inject constructor(private val dao: RecurringEntryDao) : LocalRecurringEntryService {
    override suspend fun getRecurringEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String
    ): List<RecurringEntry> {
        var o = orderBy
        if (orderBy.isEmpty()) {
            o = Entry::entryTitle.name
        }
        val s = "%$searchQuery%"
        return AsyncHelper.runAsync {
            dao.getRecurringEntries(pageId, s, o)
        }
    }

    override suspend fun getRecurringEntry(recurringEntryId: String): RecurringEntry? {
        return AsyncHelper.runAsync {
            dao.getRecurringEntry(recurringEntryId)
        }
    }

    override suspend fun insertRecurringEntries(entries: List<RecurringEntry>) {
        return AsyncHelper.runAsync {
            dao.insertRecurringEntries(entries)
        }
    }

    override suspend fun insertRecurringEntry(entry: RecurringEntry) {
        return AsyncHelper.runAsync {
            val e = entry
            dao.insertRecurringEntry(e)
        }
    }

    override suspend fun updateRecurringEntries(entries: List<RecurringEntry>) {
        return AsyncHelper.runAsync {
            dao.updateRecurringEntries(entries)
        }
    }

    override suspend fun updateRecurringEntry(entry: RecurringEntry) {
        return AsyncHelper.runAsync {
            dao.updateRecurringEntry(entry)
        }
    }

    override suspend fun deleteRecurringEntry(entry: RecurringEntry) {
        return AsyncHelper.runAsync {
            dao.deleteRecurringEntry(entry)
        }
    }

    override suspend fun deleteRecurringEntries(entries: List<RecurringEntry>) {
        return AsyncHelper.runAsync {
            dao.deleteRecurringEntries(entries)
        }
    }

    override suspend fun deleteRecurringEntries(pageId: String) {
        return AsyncHelper.runAsync {
            dao.deleteRecurringEntries(pageId)
        }
    }
}