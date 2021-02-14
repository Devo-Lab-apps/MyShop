package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalEntryMapper
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalEntryServiceImpl
@Inject constructor(
    val dao: EntryDao,
    val mapper: LocalEntryMapper
) : LocalEntryService {

    override suspend fun getEntries(pageId: String): List<Entry>? {
        return AsyncHelper.runAsync {
            mapper.entityListToPageList(dao.getEntries(pageId))
        }
    }

    override suspend fun insertEntries(entries: List<Entry>): List<Entry> {
        return AsyncHelper.runAsync {
            val e = mapper.pageListToEntityList(entries)
            dao.insertEntries(e)
            entries
        }
    }

    override suspend fun insertEntry(entry: Entry): Entry {
        return AsyncHelper.runAsync {
            val e = mapper.mapToEntity(entry)
            dao.insertEntry(e)
            entry
        }
    }

    override suspend fun updateEntries(entries: List<Entry>): List<Entry> {
        return AsyncHelper.runAsync {
            val e = mapper.pageListToEntityList(entries)
            dao.updateEntries(e)
            entries
        }
    }

    override suspend fun updateEntry(entry: Entry): Entry {
        return AsyncHelper.runAsync {
            val e = mapper.mapToEntity(entry)
            dao.updateEntry(e)
            entry
        }
    }

    override suspend fun deleteEntry(entry: Entry) {
        return AsyncHelper.runAsync {
            val e = mapper.mapToEntity(entry)
            dao.deleteEntry(e)
        }
    }

    override suspend fun deleteEntries(entries: List<Entry>) {
        return AsyncHelper.runAsync {
            val e = mapper.pageListToEntityList(entries)
            dao.deleteEntries(e)
        }
    }

    override suspend fun deleteEntries(pageId: String) {
        return AsyncHelper.runAsync {
            dao.deleteEntries(pageId)
        }
    }
}