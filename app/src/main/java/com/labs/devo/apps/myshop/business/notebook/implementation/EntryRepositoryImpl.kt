package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.helper.PermissionsHelper
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.business.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.QueryParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EntryRepositoryImpl
@Inject constructor(
    private val localEntryService: LocalEntryService,
    private val remoteEntryService: RemoteEntryService
) : EntryRepository {

    override suspend fun getEntries(
        pageId: String,
        queryParams: QueryParams
    ): Flow<DataState<List<Entry>>> = flow {

        emit(DataState.loading<List<Entry>>(true))
        try {
            checkPermissions(Permissions.GET_ENTRY)
            var localEntries = localEntryService.getEntries(pageId, queryParams)
            if (queryParams.whereQuery.isNotEmpty() && localEntries.isNullOrEmpty()) {
                throw Exception("No record for this search query.")
            }
            if (localEntries.isNullOrEmpty()) {
                val entries = remoteEntryService.getEntries(pageId)
                localEntries = localEntryService.insertEntries(entries)
            }
            emit(DataState.data(localEntries))
        } catch (ex: Exception) {
            emit(
                DataState.message<List<Entry>>(
                    ex.message ?: "An unknown error occurred. Please retry later."
                )
            )
        }
    }

    override suspend fun insertEntries(entries: List<Entry>): DataState<List<Entry>> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertedEntries = remoteEntryService.insertEntries(entries)
            val localInsertedEntries = localEntryService.insertEntries(insertedEntries)
            DataState.data(localInsertedEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertEntry(entry: Entry): DataState<Entry> {
        return try {
            checkPermissions(Permissions.CREATE_ENTRY)
            val insertEntry = remoteEntryService.insertEntry(entry)
            val localInsertedEntry = localEntryService.insertEntry(insertEntry)
            DataState.data(localInsertedEntry)
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
            val localUpdated = localEntryService.updateEntries(updatedEntries)
            DataState.data(localUpdated)
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
            val localUpdatedEntry = localEntryService.updateEntry(updatedEntry)
            DataState.data(localUpdatedEntry)
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

    override suspend fun syncEntries(pageId: String): DataState<List<Entry>> {
        return try {
            localEntryService.deleteEntries(pageId)
            val entries = remoteEntryService.getEntries(pageId)
            DataState.data(localEntryService.insertEntries(entries))
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}