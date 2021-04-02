package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteMicroEntryService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteMicroEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityMicroEntry
import com.labs.devo.apps.myshop.data.models.notebook.MicroEntry
import com.labs.devo.apps.myshop.util.exceptions.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteMicroEntryServiceFirebaseImpl @Inject constructor(
    val mapper: RemoteMicroEntryMapper
) : RemoteMicroEntryService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getMicroEntries(
        pageId: String,
        recurringEntryId: String
    ): List<MicroEntry> {
        checkIfRecurringEntryExists(pageId, recurringEntryId)
        return get(pageId, recurringEntryId)
    }

    override suspend fun insertMicroEntries(microEntries: List<MicroEntry>): List<MicroEntry> {
        if (microEntries.isNullOrEmpty()) {
            throw NoMicroEntryException()
        }
        val firstMicroEntry = microEntries.first()
        val existingEntries = get(firstMicroEntry.pageId, firstMicroEntry.recurringEntryId)
        //TODO go for approximation approach
        if (existingEntries.size > 10) {
            throw MicroEntryLimitExceededException()
        }
        val insertedEntries = mutableListOf<MicroEntry>()
        FirebaseHelper.runTransaction { transaction ->
            microEntries.forEach { microMicroEntry ->
                insertedEntries.add(
                    insertInDb(
                        microMicroEntry,
                        transaction
                    )
                )
            }
        }
        return insertedEntries
    }


    override suspend fun insertMicroEntry(microEntry: MicroEntry): MicroEntry {
        var insertedMicroEntry = microEntry.copy()
        val existingEntries = get(microEntry.pageId, microEntry.recurringEntryId)
        if (existingEntries.size > 10) {
            throw MicroEntryLimitExceededException()
        }
        FirebaseHelper.runTransaction { transaction ->
            insertedMicroEntry =
                insertInDb(microEntry, transaction)
        }
        return insertedMicroEntry
    }

    override suspend fun updateMicroEntries(microEntries: List<MicroEntry>): List<MicroEntry> {
        TODO("URGENT change this before using")
//        if (microEntries.isNullOrEmpty()) {
//            throw NoMicroEntryException()
//        }
//        val updatedEntries = mutableListOf<MicroEntry>()
//        FirebaseHelper.runTransaction { transaction ->
//            microEntries.forEach { microEntry ->
//                updatedEntries.add(
//                    updateInDb(
//                        microEntry,
//                        transaction,
//
//                        System.currentTimeMillis()
//                    )
//                )
//            }
//        }
//        return updatedEntries
    }


    override suspend fun updateMicroEntry(createdAt: Long, microEntry: MicroEntry): MicroEntry {
        var updatedMicroEntry = microEntry.copy()
        FirebaseHelper.runTransaction { transaction ->
            updatedMicroEntry =
                updateInDb(microEntry, transaction, createdAt)
        }
        return updatedMicroEntry
    }

    override suspend fun deleteMicroEntry(createdAt: Long, microEntry: MicroEntry) {
        FirebaseHelper.runTransaction { transaction ->
            deleteFromDb(createdAt, microEntry, transaction)
        }
    }

    override suspend fun deleteMicroEntries(microEntries: List<MicroEntry>) {
        //TODO("What to do here")
//        if (microEntries.isNullOrEmpty()) {
//            throw NoMicroEntryException()
//        }
//        FirebaseHelper.runTransaction { transaction ->
//            microEntries.forEach { microEntry ->
//                deleteFromDb(microEntry, transaction)
//            }
//        }
    }


    private fun insertInDb(
        microEntry: MicroEntry,
        transaction: Transaction
    ): MicroEntry {
        return if (checkIdMicroEntryExists(microEntry, transaction)) {
            val ref = FirebaseHelper.getMicroEntryReference(
                microEntry.pageId,
                microEntry.recurringEntryId,
                microEntry.amount.toString()
            )
            val createdAt = "${System.currentTimeMillis()}"
            val modifiedAt = System.currentTimeMillis()
            val data: MutableMap<String, Any> = mutableMapOf()
            data[MicroEntry::createdAt.name] =
                FieldValue.arrayUnion(createdAt)
            data[MicroEntry::count.name] = FieldValue.increment(1)
            transaction.update(ref, data)
            microEntry.createdAt[createdAt] = modifiedAt
            microEntry
        } else {
            val ref = FirebaseHelper.getMicroEntryReference(
                microEntry.pageId,
                microEntry.recurringEntryId,
                microEntry.amount.toString()
            )
            if (microEntry.createdAt.isEmpty()) {
                val createdAt = "${System.currentTimeMillis()}"
                val modifiedAt = System.currentTimeMillis()
                microEntry.createdAt[createdAt] = modifiedAt
            }
            transaction.set(ref, mapper.mapToEntity(microEntry))
            microEntry
        }
    }

    private fun checkIdMicroEntryExists(microEntry: MicroEntry, transaction: Transaction): Boolean {
        val ref = FirebaseHelper.getMicroEntryReference(
            microEntry.pageId,
            microEntry.recurringEntryId,
            microEntry.amount.toString()
        )
        val ds = transaction.get(ref)
        return ds.exists()
    }


    private fun updateInDb(
        microEntry: MicroEntry,
        transaction: Transaction,
        createdAt: Long
    ): MicroEntry {
        TODO("Is it really needed")
//        val ref = FirebaseHelper.getMicroEntryReference(
//            microEntry.pageId,
//            microEntry.recurringEntryId,
//            microEntry.amount.toString()
//        )
//        val ds = transaction.get(ref)
//        if (!ds.exists()) throw MicroEntryNotFoundException()
//        val existingMicroEntry = ds.toObject(RemoteEntityMicroEntry::class.java)!!
//        if (createdAt.toString() !in existingMicroEntry.createdAt) {
//            throw EntryNotFoundException()
//        }
//        val entity = mapper.mapToEntity(microEntry)
//        val modifiedAt = System.currentTimeMillis()
//        entity.createdAt[createdAt.toString()] = modifiedAt
//        microEntry.createdAt[createdAt.toString()] = modifiedAt
//        transaction.set(ref, entity)
//        return microEntry
    }

    private fun deleteFromDb(
        createdAt: Long,
        microEntry: MicroEntry,
        transaction: Transaction
    ): MicroEntry {
        return if (checkIdMicroEntryExists(microEntry, transaction)) {
            val ref = FirebaseHelper.getMicroEntryReference(
                microEntry.pageId,
                microEntry.recurringEntryId,
                microEntry.amount.toString()
            )
            val ds = transaction.get(ref)
            if (!ds.exists()) throw MicroEntryNotFoundException()
            val existingMicroEntry = ds.toObject(RemoteEntityMicroEntry::class.java)!!
            if (createdAt.toString() !in existingMicroEntry.createdAt) {
                throw EntryNotFoundException()
            } else {
                existingMicroEntry.createdAt.remove(createdAt.toString())
                microEntry.createdAt.remove(createdAt.toString())
            }
            microEntry
        } else {
            throw MicroEntryNotFoundException()
        }
    }

    private suspend fun get(pageId: String, recurringEntryId: String): List<MicroEntry> {
        val ss = FirebaseHelper.getMicroEntryCollection(pageId, recurringEntryId).get().await()
        return ss?.documents?.map { ds ->
            val obj = ds.toObject(RemoteEntityMicroEntry::class.java)!!
            mapper.mapFromEntity(obj)
        } ?: listOf()
    }

    private suspend fun checkIfRecurringEntryExists(pageId: String, recurringEntryId: String) {
        val obj = FirebaseHelper.getRecurringEntryReference(pageId, recurringEntryId).get().await()
        if (!obj.exists()) {
            throw RecurringEntryNotFoundException()
        }
    }
}