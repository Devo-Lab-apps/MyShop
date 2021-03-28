package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import com.labs.devo.apps.myshop.business.helper.PermissionsHelper
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalRecurringEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryNotFoundException
import com.labs.devo.apps.myshop.view.util.DataState
import javax.inject.Inject

class RecurringEntryRepositoryImpl
@Inject constructor(
    private val localRecurringEntryService: LocalRecurringEntryService,
    private val remoteRecurringEntryService: RemoteRecurringEntryService
) : RecurringEntryRepository {

    override suspend fun getRecurringEntries(pageId: String): DataState<List<RecurringEntry>> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.GET_RECURRING_ENTRY)
            var recurringEntries = localRecurringEntryService.getRecurringEntries(pageId)
            if (recurringEntries.isNullOrEmpty()) {
                val remoteRecurringEntries = remoteRecurringEntryService.getRecurringEntries(pageId)
                localRecurringEntryService.insertRecurringEntries(remoteRecurringEntries)
                recurringEntries = remoteRecurringEntries
            }
            DataState.data(recurringEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun getRecurringEntry(recurringEntryId: String): DataState<RecurringEntry> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.GET_RECURRING_ENTRY)
            val localRecurringEntry = localRecurringEntryService.getRecurringEntry(recurringEntryId)
            DataState.data(localRecurringEntry ?: throw RecurringEntryNotFoundException())
        } catch (ex: java.lang.Exception) {
            DataState.message(ex.message ?: "An unknown error occurred. Please retry later.")
        }
    }

    override suspend fun insertRecurringEntries(entries: List<RecurringEntry>): DataState<List<RecurringEntry>> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_RECURRING_ENTRY)
            val insertedRecurringEntries =
                remoteRecurringEntryService.insertRecurringEntries(entries)
            localRecurringEntryService.insertRecurringEntries(insertedRecurringEntries)
            DataState.data(insertedRecurringEntries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertRecurringEntry(recurringEntry: RecurringEntry): DataState<RecurringEntry> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_RECURRING_ENTRY)
            val insertRecurringEntry =
                remoteRecurringEntryService.insertRecurringEntry(recurringEntry)
            localRecurringEntryService.insertRecurringEntry(insertRecurringEntry)
            DataState.data(insertRecurringEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateRecurringEntries(entries: List<RecurringEntry>): DataState<List<RecurringEntry>> {

        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_RECURRING_ENTRY)
            val updatedRecurringEntries =
                remoteRecurringEntryService.updateRecurringEntries(entries)
            localRecurringEntryService.updateRecurringEntries(updatedRecurringEntries)
            DataState.data(updatedRecurringEntries)
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

    override suspend fun deleteRecurringEntries(entries: List<RecurringEntry>): DataState<List<RecurringEntry>> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_RECURRING_ENTRY)
            remoteRecurringEntryService.deleteRecurringEntries(entries)
            localRecurringEntryService.deleteRecurringEntries(entries)
            DataState.data(entries)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun syncRecurringEntries(pageId: String): DataState<List<RecurringEntry>> {
        return try {
            localRecurringEntryService.deleteRecurringEntries(pageId)
            val recurringEntries = remoteRecurringEntryService.getRecurringEntries(pageId)
            localRecurringEntryService.insertRecurringEntries(recurringEntries)
            DataState.data(recurringEntries)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}