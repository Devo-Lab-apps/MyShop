package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalRecurringEntryService
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.mediator.RecurringEntryRemoteMediator
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryNotFoundException
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val RECURRING_ENTRY_PAGE_SIZE = 20
const val RECURRING_ENTRY_MAX_SIZE = 100

class RecurringEntryRepositoryImpl
@Inject constructor(
    private val localRecurringEntryService: LocalRecurringEntryService,
    private val remoteRecurringEntryService: RemoteRecurringEntryService,
    private val notebookDatabase: NotebookDatabase
) : RecurringEntryRepository {

    override suspend fun getRecurringEntries(
        pageId: String?,
        forceRefresh: Boolean
    ): Flow<PagingData<RecurringEntry>> = Pager(
        config = PagingConfig(RECURRING_ENTRY_PAGE_SIZE, RECURRING_ENTRY_MAX_SIZE),
        remoteMediator = RecurringEntryRemoteMediator(
            pageId,
            forceRefresh,
            notebookDatabase,
            remoteRecurringEntryService
        ),
        pagingSourceFactory = {
            localRecurringEntryService.getRecurringEntries(pageId ?: "%%")
        }
    ).flow
//    : DataState<List<RecurringEntry>> {
//        return try {
//            PermissionsHelper.checkPermissions(Permissions.GET_RECURRING_ENTRY)
//            val lastFetchedRecurringEntry =
//                localRecurringEntryService.getLastFetchedRecurringEntry(pageId)
//            lastFetchedRecurringEntry?.let { re ->
//                if (re.fetchedAt < System.currentTimeMillis() - ONE_DAY_MILLIS)
//                    return syncRecurringEntries(pageId)
//            }
//            var recurringEntries = localRecurringEntryService.getRecurringEntries(pageId)
//            if (recurringEntries.isNullOrEmpty()) {
//                val remoteRecurringEntries = remoteRecurringEntryService.getRecurringEntries(pageId)
//                localRecurringEntryService.insertRecurringEntries(remoteRecurringEntries)
//                recurringEntries = remoteRecurringEntries
//            }
//            DataState.data(recurringEntries)
//        } catch (ex: Exception) {
//            DataState.message(
//                ex.message ?: "An unknown error occurred. Please retry later."
//            )
//        }
//    }

    override suspend fun getRecurringEntry(recurringEntryId: String): DataState<RecurringEntry> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.GET_RECURRING_ENTRY)
            val localRecurringEntry = localRecurringEntryService.getRecurringEntry(recurringEntryId)
            DataState.data(localRecurringEntry ?: throw RecurringEntryNotFoundException())
        } catch (ex: java.lang.Exception) {
            DataState.message(ex.message ?: "An unknown error occurred. Please retry later.")
        }
    }

    override suspend fun createRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_RECURRING_ENTRY)
            val insertRecurringEntry =
                remoteRecurringEntryService.createRecurringEntry(recurringEntry)
            localRecurringEntryService.createRecurringEntry(insertRecurringEntry)
            DataState.data(insertRecurringEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry> {

        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_RECURRING_ENTRY)
            val updatedRecurringEntry =
                remoteRecurringEntryService.updateRecurringEntry(recurringEntry)
            localRecurringEntryService.updateRecurringEntry(updatedRecurringEntry)
            DataState.data(updatedRecurringEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_RECURRING_ENTRY)
            remoteRecurringEntryService.deleteRecurringEntry(recurringEntry)
            localRecurringEntryService.deleteRecurringEntry(recurringEntry)
            DataState.data(recurringEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteRecurringEntries() {
        localRecurringEntryService.deleteRecurringEntries()
    }

    override suspend fun syncRecurringEntries(pageId: String): DataState<List<RecurringEntry>> {
        return try {
            localRecurringEntryService.deleteRecurringEntries(pageId)
            val recurringEntries = remoteRecurringEntryService.getRecurringEntries(pageId, "")
            localRecurringEntryService.createRecurringEntries(recurringEntries)
            DataState.data(recurringEntries)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}