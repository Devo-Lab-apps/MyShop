package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteRecurringEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityRecurringEntry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.util.exceptions.NoRecurringEntryException
import com.labs.devo.apps.myshop.util.exceptions.PageNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryLimitExceededException
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryNotFoundException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteRecurringEntryServiceFirebaseImpl
@Inject constructor(private val mapper: RemoteRecurringEntryMapper) :
    RemoteRecurringEntryService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getRecurringEntries(
        pageId: String
    ): List<RecurringEntry> {
        checkIfPageExists(pageId)
        return get(pageId)
    }

    override suspend fun insertRecurringEntries(recurringEntries: List<RecurringEntry>): List<RecurringEntry> {
        if (recurringEntries.isNullOrEmpty()) {
            throw NoRecurringEntryException()
        }
        val firstEntry = recurringEntries.first()
        val existingEntries = get(firstEntry.pageId)
        if (existingEntries.size > 1) {
            throw RecurringEntryLimitExceededException()
        }
        val insertedEntries = mutableListOf<RecurringEntry>()
        FirebaseHelper.runTransaction { transaction ->
            recurringEntries.forEach { recurringRecurringEntry ->
                insertedEntries.add(
                    insertInDb(
                        recurringRecurringEntry.pageId,
                        recurringRecurringEntry,
                        transaction
                    )
                )
            }
        }
        return insertedEntries
    }


    override suspend fun insertRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry {
        var insertedRecurringEntry = recurringEntry.copy()
        val existingEntries = get(recurringEntry.pageId)
        if (existingEntries.size > 1) {
            throw RecurringEntryLimitExceededException()
        }
        FirebaseHelper.runTransaction { transaction ->
            insertedRecurringEntry =
                insertInDb(recurringEntry.pageId, recurringEntry, transaction)
        }
        return insertedRecurringEntry
    }

    override suspend fun updateRecurringEntries(recurringEntries: List<RecurringEntry>): List<RecurringEntry> {
        if (recurringEntries.isNullOrEmpty()) {
            throw NoRecurringEntryException()
        }
        val firstRecurringEntry = recurringEntries.first()
        val updatedEntries = mutableListOf<RecurringEntry>()
        FirebaseHelper.runTransaction { transaction ->
            recurringEntries.forEach { recurringEntry ->
                updatedEntries.add(
                    updateInDb(
                        firstRecurringEntry.pageId,
                        recurringEntry,
                        transaction
                    )
                )
            }
        }
        return updatedEntries
    }


    override suspend fun updateRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry {
        var updatedRecurringEntry = recurringEntry.copy()
        FirebaseHelper.runTransaction { transaction ->
            updatedRecurringEntry =
                updateInDb(recurringEntry.pageId, recurringEntry, transaction)
        }
        return updatedRecurringEntry
    }

    override suspend fun deleteRecurringEntry(recurringEntry: RecurringEntry) {
        FirebaseHelper.runTransaction { transaction ->
            deleteFromDb(recurringEntry.pageId, recurringEntry, transaction)
        }
    }

    override suspend fun deleteRecurringEntries(recurringEntries: List<RecurringEntry>) {
        if (recurringEntries.isNullOrEmpty()) {
            throw NoRecurringEntryException()
        }
        FirebaseHelper.runTransaction { transaction ->
            recurringEntries.forEach { recurringRecurringEntry ->
                deleteFromDb(recurringRecurringEntry.pageId, recurringRecurringEntry, transaction)
            }
        }
    }


    private fun insertInDb(
        pageId: String,
        recurringEntry: RecurringEntry,
        transaction: Transaction
    ): RecurringEntry {
        val id = FirebaseHelper.getRecurringEntryReference(pageId).id
        val ref = FirebaseHelper.getRecurringEntryReference(pageId, id)
        val data = mapper.mapToEntity(recurringEntry)
        data.recurringEntryId = id
        recurringEntry.recurringEntryId = id
        transaction.set(ref, data)
        return recurringEntry
    }


    private fun updateInDb(
        pageId: String,
        recurringEntry: RecurringEntry,
        transaction: Transaction
    ): RecurringEntry {
        val ref = FirebaseHelper.getRecurringEntryReference(
            pageId,
            recurringEntry.recurringEntryId
        )
        checkIfRecurringEntryExists(
            transaction,
            pageId,
            recurringEntry.recurringEntryId
        )
        val data = mapper.mapToEntity(recurringEntry)
        transaction.set(ref, data)
        return recurringEntry
    }

    private fun deleteFromDb(
        pageId: String,
        recurringRecurringEntry: RecurringEntry,
        transaction: Transaction
    ): RecurringEntry {
        val ref = FirebaseHelper.getRecurringEntryReference(
            pageId,
            recurringRecurringEntry.recurringEntryId
        )
        checkIfRecurringEntryExists(
            transaction,
            pageId,
            recurringRecurringEntry.recurringEntryId
        )
        transaction.delete(ref)
        return recurringRecurringEntry
    }

    private suspend fun get(pageId: String): List<RecurringEntry> {
        val ss = FirebaseHelper.getRecurringEntryCollection(pageId).get().await()
        return ss?.documents?.map { ds ->
            val obj = ds.toObject(RemoteEntityRecurringEntry::class.java)!!
            mapper.mapFromEntity(obj)
        } ?: listOf()
    }

    private suspend fun checkIfPageExists(pageId: String) {
        val obj = FirebaseHelper.getPageReference(pageId).get().await()
        if (!obj.exists()) {
            throw PageNotFoundException()
        }
    }

    private fun checkIfRecurringEntryExists(
        transaction: Transaction,
        pageId: String,
        recurringRecurringEntryId: String
    ) {
        val ref = FirebaseHelper.getRecurringEntryReference(pageId, recurringRecurringEntryId)
        val obj = transaction.get(ref)
        if (!obj.exists()) {
            throw RecurringEntryNotFoundException()
        }
    }
}