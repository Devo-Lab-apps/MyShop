package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.EntryDao
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalEntryServiceImpl
@Inject constructor(
    val dao: EntryDao,
) : LocalEntryService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry> {
        var finalOrderBy = orderBy
        if (orderBy.isEmpty()) {
            finalOrderBy = Entry::entryTitle.name
        }
        val s = "%$searchQuery%"
        return dao.getEntries(pageId, s, finalOrderBy, isRepeating)
    }

    override fun getEntriesLikeEntryId(
        entryId: String,
        dateRange: Pair<Long, Long>,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry> {
        var o = orderBy
        if (orderBy.isEmpty()) {
            o = Entry::modifiedAt.name
        }
        return dao.getEntriesLikeEntryId("$entryId%", dateRange, o, isRepeating)
    }

    override fun getEntriesTotalAmount(pageId: String): LiveData<Double> {
         return dao.getEntriesTotalAmount(pageId)
    }

    override suspend fun getEntry(entryId: String): Entry? {
        return AsyncHelper.runAsync {
            dao.getEntry(entryId)
        }
    }

    override suspend fun createEntries(entries: List<Entry>) {
        return AsyncHelper.runAsync {
            val e = entries
            dao.createEntries(e)
        }
    }

    override suspend fun createEntry(entry: Entry) {
        return AsyncHelper.runAsync {
            val e = entry
            dao.createEntry(e)
        }
    }

    override suspend fun updateEntry(entry: Entry) {
        return AsyncHelper.runAsync {
            val e = entry
            dao.updateEntry(e)
        }
    }

    override suspend fun deleteEntry(entry: Entry) {
        return AsyncHelper.runAsync {
            val e = entry
            dao.deleteEntry(e)
        }
    }

    override suspend fun deleteEntries(pageId: String) {
        return AsyncHelper.runAsync {
            dao.deleteEntries(pageId)
        }
    }

    override suspend fun deleteEntriesLikeEntryId(entryId: String) {
        return AsyncHelper.runAsync {
            dao.deleteEntriesLikeEntryId("$entryId%")
        }
    }

    override suspend fun deleteEntries() {
        AsyncHelper.runAsync {
            dao.deleteAll()
        }
    }
}