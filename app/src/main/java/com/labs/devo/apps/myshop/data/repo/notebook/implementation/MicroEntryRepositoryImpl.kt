package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteMicroEntryService
import com.labs.devo.apps.myshop.data.mediator.*
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.MicroEntryRepository
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MicroEntryRepositoryImpl @Inject constructor(
    private val localEntryService: LocalEntryService,
    private val notebookDatabase: NotebookDatabase,
    private val remoteEntryService: RemoteMicroEntryService
) : MicroEntryRepository {

    @ExperimentalPagingApi
    override suspend fun getMicroEntries(
        pageId: String,
        recurringEntry: RecurringEntry,
        dateRange: Pair<Long, Long>,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Entry>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100),
        remoteMediator = MicroEntryRemoteMediator(
            pageId,
            recurringEntry,
            dateRange,
            orderBy,
            forceRefresh,
            notebookDatabase,
            remoteEntryService
        ),
        pagingSourceFactory = {
            localEntryService.getEntriesLikeEntryId(
                pageId + "$$" + recurringEntry.recurringEntryId,
                dateRange,
                orderBy,
                true
            )
        }
    ).flow

//    override suspend fun getEntry(microEntryId: String): DataState<Entry> {
//        return try {
//            checkPermissions(Permissions.GET_ENTRY)
//            var entry = localEntryService.getEntry(entryId)
//            if (entry == null) {
//                //TODO set this f
//                entry = remoteEntryService.getMicroEntry(entryId, "", "")[0]
//            }
//            DataState.data(entry)
//        } catch (ex: Exception) {
//            DataState.message(
//                ex.message ?: "An unknown error occurred. Please retry later."
//            )
//        }
//    }

//    flow
//    {
//
//        emit(DataState.loading<List<Entry>>(true))
//        try {
//            checkPermissions(Permissions.GET_ENTRY)
//            var localEntries = localEntryService.getEntries(pageId, queryParams)
//            if (queryParams.whereQuery.isNotEmpty() && localEntries.isNullOrEmpty()) {
//                throw Exception("No record for this search query.")
//            }
//            if (localEntries.isNullOrEmpty()) {
//                val entries = remoteEntryService.getEntries(pageId)
//                localEntries = localEntryService.insertEntries(entries)
//            }
//            emit(DataState.data(localEntries))
//        } catch (ex: Exception) {
//            emit(
//                DataState.message<List<Entry>>(
//                    ex.message ?: "An unknown error occurred. Please retry later."
//                )
//            )
//        }
//    }

    override suspend fun insertMicroEntries(
        recurringEntry: RecurringEntry,
        entries: List<Entry>
    ): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val microEntries = convertEntriesToMicroEntries(recurringEntry, entries)
            val insertedMicroEntries = remoteEntryService.insertMicroEntries(microEntries)
            val createdEntries = convertMicroEntriesToEntries(recurringEntry, insertedMicroEntries)
            localEntryService.insertEntries(createdEntries)
            DataState.data(createdEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertMicroEntry(
        recurringEntry: RecurringEntry,
        entry: Entry
    ): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertedMicroEntry =
                remoteEntryService.insertMicroEntry(convertEntryToMicroEntry(recurringEntry, entry))
            //Entry created right now
            val insertedEntry =
                convertMicroEntryToEntry(recurringEntry, insertedMicroEntry).sortedByDescending {
                    it.createdAt
                }[0]
            localEntryService.insertEntry(insertedEntry)
            DataState.data(insertedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateMicroEntries(
        recurringEntry: RecurringEntry,
        entries: List<Entry>
    ): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val updatedMicroEntries = remoteEntryService.updateMicroEntries(
                convertEntriesToMicroEntries(
                    recurringEntry,
                    entries
                )
            )
            val updatedEntries = convertMicroEntriesToEntries(recurringEntry, updatedMicroEntries)
            localEntryService.updateEntries(updatedEntries)
            DataState.data(updatedEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateMicroEntry(
        recurringEntry: RecurringEntry,
        entry: Entry
    ): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val createdAt = entry.entryId.split("$$")[2].toLong()
            val updatedMicroEntry =
                remoteEntryService.updateMicroEntry(
                    createdAt,
                    convertEntryToMicroEntry(recurringEntry, entry)
                )
            val updatedEntry = convertMicroEntryToEntry(
                recurringEntry,
                updatedMicroEntry
            ).sortedByDescending { it.createdAt }[0]
            localEntryService.updateEntry(updatedEntry)
            DataState.data(updatedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteMicroEntry(
        recurringEntry: RecurringEntry,
        entry: Entry
    ): DataState<Entry> {
        return try {
            checkPermissions(Permissions.DELETE_ENTRY)
            val createdAt = entry.entryId.split("$$")[2].toLong()
            remoteEntryService.deleteMicroEntry(
                createdAt,
                convertEntryToMicroEntry(recurringEntry, entry)
            )
            localEntryService.deleteEntry(entry)
            DataState.data(entry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteMicroEntries(
        recurringEntry: RecurringEntry,
        entries: List<Entry>
    ): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.DELETE_ENTRY)
            remoteEntryService.deleteMicroEntries(
                convertEntriesToMicroEntries(
                    recurringEntry,
                    entries
                )
            )
            localEntryService.deleteEntries(entries)
            DataState.data(entries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }


}