package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.const.AppConstants.TAG
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.mediator.EntryRemoteMediator
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EntryRepositoryImpl
@Inject constructor(
    private val localEntryService: LocalEntryService,
    private val notebookDatabase: NotebookDatabase,
    private val remoteEntryService: RemoteEntryService
) : EntryRepository {

    @ExperimentalPagingApi
    override suspend fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean,
        isRepeating: Boolean
    ): Flow<PagingData<Entry>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100),
        remoteMediator = EntryRemoteMediator(
            pageId,
            searchQuery,
            forceRefresh,
            notebookDatabase,
            remoteEntryService
        ),
        pagingSourceFactory = {
            localEntryService.getEntries(
                pageId,
                searchQuery,
                orderBy,
                isRepeating
            )
        }
    ).flow

    override suspend fun getEntry(entryId: String): DataState<Entry> {
        return try {
            checkPermissions(Permissions.GET_ENTRY)
            var entry = localEntryService.getEntry(entryId)
            if (entry == null) {
                //TODO set this f
                entry = remoteEntryService.getEntries(entryId, "", "")[0]
            }
            DataState.data(entry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

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

    override suspend fun insertEntries(entries: List<Entry>): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertedEntries = remoteEntryService.insertEntries(entries)
            localEntryService.insertEntries(insertedEntries)
            DataState.data(insertedEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertEntry(entry: Entry): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertedEntry = remoteEntryService.insertEntry(entry)
            localEntryService.insertEntry(insertedEntry)
            DataState.data(insertedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateEntries(entries: List<Entry>): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val updatedEntries = remoteEntryService.updateEntries(entries)
            localEntryService.updateEntries(updatedEntries)
            DataState.data(updatedEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateEntry(entry: Entry): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val updatedEntry = remoteEntryService.updateEntry(entry)
            localEntryService.updateEntry(updatedEntry)
            DataState.data(updatedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteEntry(entry: Entry): DataState<Entry> {
        return try {
            checkPermissions(Permissions.DELETE_ENTRY)
            remoteEntryService.deleteEntry(entry)
            localEntryService.deleteEntry(entry)
            DataState.data(entry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteEntries(entries: List<Entry>): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.DELETE_ENTRY)
            remoteEntryService.deleteEntries(entries)
            localEntryService.deleteEntries(entries)
            DataState.data(entries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteEntries() {
        localEntryService.deleteEntries()
    }

    override suspend fun syncEntries(pageId: String): DataState<List<Entry>> {
        return try {
            localEntryService.deleteEntries(pageId)
            val entries = remoteEntryService.getEntries(pageId, "", "")
            localEntryService.insertEntries(entries)
            DataState.data(entries)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override fun getEntriesTotalAmount(pageId: String): LiveData<Double> {
        return localEntryService.getEntriesTotalAmount(pageId)
    }
}