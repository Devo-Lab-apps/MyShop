package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteMicroEntryService
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.MicroEntry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import javax.inject.Inject

const val recurringLoadKey = "recurring"

class MicroEntryRemoteMediator @Inject constructor(
    private val pageId: String,
    private val recurringEntry: RecurringEntry,
    private val searchQuery: String,
    private val orderBy: String,
    private val forceRefresh: Boolean,
    private val database: NotebookDatabase,
    private val networkService: RemoteMicroEntryService
) : RemoteMediator<Int, Entry>() {

    val entryService = database.entryDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Entry>): MediatorResult {
        try {
            val remoteKey =
                "$entryLoadKey$pageId$recurringLoadKey${recurringEntry.recurringEntryId}"
            when (loadType) {
                LoadType.REFRESH -> {

                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val rk = database.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(remoteKey)
                    }
                    if (rk != null) {
                        if (rk.nextKey == true.toString()) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                    }
                }
            }

            val remoteEntries =
                networkService.getMicroEntries(pageId, recurringEntry.recurringEntryId)
            val entries = convertMicroEntriesToEntries(recurringEntry, remoteEntries)

            val endReached = remoteEntries.isNotEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(remoteKey)
                    entryService.deleteEntries(entries)
                }

                remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, endReached.toString()))
                entryService.insertEntries(entries)
            }
            //TODO put appropriate condition
            return MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (forceRefresh) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

}


fun convertMicroEntriesToEntries(
    recurringEntry: RecurringEntry,
    remoteEntries: List<MicroEntry>
): List<Entry> {
    val list = mutableListOf<Entry>()
    //TODO add work manager here to note frequency.
    for (r in remoteEntries) {
        for (c in r.createdAt) {
            list.add(
                Entry(
                    r.pageId,
                    recurringEntry.recurringEntryId + "$$" + c.key,
                    recurringEntry.name,
                    recurringEntry.description,
                    r.amount,
                    true,
                    mapOf(),
                    c.key.toLong(),
                    c.value
                )
            )
        }
    }
    return list
}

fun convertMicroEntryToEntry(
    recurringEntry: RecurringEntry,
    remoteEntry: MicroEntry
): List<Entry> {
    val list = mutableListOf<Entry>()
    //TODO add work manager here to note frequency.
    for (c in remoteEntry.createdAt) {
        list.add(
            Entry(
                recurringEntry.pageId,
                recurringEntry.recurringEntryId + "$$" + c.key,
                recurringEntry.name,
                recurringEntry.description,
                remoteEntry.amount,
                true,
                mapOf(),
                c.key.toLong(),
                c.value
            )
        )
    }
    return list
}

fun convertEntriesToMicroEntries(
    recurringEntry: RecurringEntry,
    entries: List<Entry>
): List<MicroEntry> {
    val list = mutableListOf<MicroEntry>()
    val entriesByAmount = entries.groupBy { it.entryAmount }
    for (entryByAmount in entriesByAmount) {
        val mutableMap = mutableMapOf<String, Long>()
        for (entry in entryByAmount.value) {
            mutableMap[entry.createdAt.toString()] = entry.modifiedAt
        }
        list.add(
            MicroEntry(
                entryByAmount.value.size,
                entryByAmount.key,
                recurringEntryId = recurringEntry.recurringEntryId,
                pageId = recurringEntry.pageId,
                createdAt = mutableMap
            )
        )
    }
    return list
}


fun convertEntryToMicroEntry(
    recurringEntry: RecurringEntry,
    entry: Entry
): MicroEntry {
    val mutableMap = mutableMapOf<String, Long>()
    mutableMap[entry.createdAt.toString()] = entry.modifiedAt
    return MicroEntry(
        1,
        entry.entryAmount,
        recurringEntryId = recurringEntry.recurringEntryId,
        pageId = recurringEntry.pageId,
        createdAt = mutableMap
    )
}
