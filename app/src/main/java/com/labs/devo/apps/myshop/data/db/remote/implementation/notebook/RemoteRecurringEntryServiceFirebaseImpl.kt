package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteRecurringEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityRecurringEntry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.util.exceptions.PageNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryLimitExceededException
import com.labs.devo.apps.myshop.util.exceptions.RecurringEntryNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteRecurringEntryServiceFirebaseImpl
@Inject constructor(private val mapper: RemoteRecurringEntryMapper) :
    RemoteRecurringEntryService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getRecurringEntries(
        pageId: String?,
        startAfter: String
    ): List<RecurringEntry> {
        checkIfPageExists(pageId)
        return get(pageId, startAfter)
    }

    override suspend fun insertRecurringEntry(recurringEntry: RecurringEntry): RecurringEntry {
        var insertedRecurringEntry = recurringEntry.copy()
        val existingEntries = get(recurringEntry.pageId, "")
        if (existingEntries.size > 1) {
            throw RecurringEntryLimitExceededException()
        }
        FirebaseHelper.runTransaction { transaction ->
            insertedRecurringEntry =
                insertInDb(recurringEntry.pageId, recurringEntry, transaction)
        }
        return insertedRecurringEntry
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

    private fun insertInDb(
        pageId: String,
        recurringEntry: RecurringEntry,
        transaction: Transaction
    ): RecurringEntry {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = FirebaseHelper.getRecurringEntryReference(user.accountId).id
        val ref = FirebaseHelper.getRecurringEntryReference(user.accountId, id)
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
        val user = UserManager.user ?: throw UserNotInitializedException()
        val ref = FirebaseHelper.getRecurringEntryReference(
            user.accountId,
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
        val user = UserManager.user ?: throw UserNotInitializedException()
        val ref = FirebaseHelper.getRecurringEntryReference(
            user.accountId,
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

    private suspend fun get(pageId: String?, startAfter: String): List<RecurringEntry> {
        val user = UserManager.user ?: throw UserNotInitializedException()

        if (pageId != null) {
            val ref = FirebaseHelper.getRecurringEntryCollection(user.accountId).whereEqualTo(
                RecurringEntry::recurringEntryId.name,
                pageId
            )
            val ss = ref.get().await()
            return ss?.documents?.map { ds ->
                val obj = ds.toObject(RemoteEntityRecurringEntry::class.java)!!
                mapper.mapFromEntity(obj)
            } ?: listOf()
        } else {
            val ref = FirebaseHelper.getRecurringEntryCollection(user.accountId).orderBy(
                RecurringEntry::recurringEntryId.name
            ).startAfter(startAfter).limit(10)
            val ss = ref.get().await()
            return ss?.documents?.map { ds ->
                val obj = ds.toObject(RemoteEntityRecurringEntry::class.java)!!
                mapper.mapFromEntity(obj)
            } ?: listOf()
        }
    }

    private suspend fun checkIfPageExists(pageId: String?) {
        if (pageId == null) return
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
        val user = UserManager.user ?: throw UserNotInitializedException()
        val ref = FirebaseHelper.getRecurringEntryReference(
            user.accountId,
            recurringRecurringEntryId
        )
        val obj = transaction.get(ref)
        if (!obj.exists()) {
            throw RecurringEntryNotFoundException()
        }
    }
}