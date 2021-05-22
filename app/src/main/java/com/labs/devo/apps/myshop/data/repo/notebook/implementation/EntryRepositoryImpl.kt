package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.database.database.AppDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.mediator.EntryRemoteMediator
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


const val ENTRY_PAGE_SIZE = 20
const val ENTRY_MAX_SIZE = 100
class EntryRepositoryImpl
@Inject constructor(
    private val localEntryService: LocalEntryService,
    private val notebookDatabase: NotebookDatabase,
    private val appDatabase: AppDatabase,
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
        config = PagingConfig(ENTRY_PAGE_SIZE, ENTRY_MAX_SIZE),
        remoteMediator = EntryRemoteMediator(
            pageId,
            searchQuery,
            forceRefresh,
            notebookDatabase,
            appDatabase,
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

    override suspend fun getEntry(pageId: String, entryId: String): DataState<Entry> {
        return try {
            checkPermissions(Permissions.GET_ENTRY)
            var entry = localEntryService.getEntry(entryId)
            if (entry == null) {
                entry = remoteEntryService.getEntry(pageId, entryId)
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

    override suspend fun createEntry(entry: Entry): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertedEntry = remoteEntryService.createEntry(entry)
            localEntryService.createEntry(insertedEntry)
            DataState.data(insertedEntry)
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


    override suspend fun deleteEntries() {
        localEntryService.deleteEntries()
    }

    override suspend fun syncEntries(pageId: String): DataState<List<Entry>> {
        return try {
            localEntryService.deleteEntries(pageId)
            val entries = remoteEntryService.getEntries(pageId, "", "")
            localEntryService.createEntries(entries)
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