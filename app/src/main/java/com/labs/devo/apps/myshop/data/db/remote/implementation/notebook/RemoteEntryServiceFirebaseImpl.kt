package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityEntry
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.util.exceptions.EntryNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.NoEntryException
import com.labs.devo.apps.myshop.util.exceptions.PageNotFoundException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteEntryServiceFirebaseImpl
@Inject constructor(val mapper: RemoteEntryMapper) : RemoteEntryService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getEntries(
        pageId: String,
        query: String,
        startAfter: String?
    ): List<Entry> {
        checkIfPageExists(pageId)
        return get(pageId, query, startAfter)
    }

    override suspend fun getEntry(pageId: String, entryId: String): Entry {
        checkIfPageExists(pageId)
        return get(pageId, entryId)
    }


    override suspend fun createEntry(entry: Entry): Entry {
        var insertedEntry = entry.copy()
        FirebaseHelper.runTransaction { transaction ->
            insertedEntry = createInDb(entry.pageId, entry, transaction)
        }
        return insertedEntry
    }

    override suspend fun updateEntry(entry: Entry): Entry {
        var updatedEntry = entry.copy()
        FirebaseHelper.runTransaction { transaction ->
            updatedEntry = updateInDb(entry.pageId, entry, transaction)
        }
        return updatedEntry
    }

    override suspend fun deleteEntry(entry: Entry) {
        FirebaseHelper.runTransaction { transaction ->
            deleteFromDb(entry.pageId, entry, transaction)
        }
    }


    private fun createInDb(pageId: String, entry: Entry, transaction: Transaction): Entry {
        val id = FirebaseHelper.getEntryReference(pageId).id
        val ref = FirebaseHelper.getEntryReference(pageId, id)
        val data = mapper.mapToEntity(entry)
        data.entryId = id
        transaction.set(ref, data)
        return mapper.mapFromEntity(data)
    }


    private fun updateInDb(pageId: String, entry: Entry, transaction: Transaction): Entry {
        val ref = FirebaseHelper.getEntryReference(pageId, entry.entryId)
        checkIfEntryExists(transaction, pageId, entry.entryId)
        val data = mapper.mapToEntity(entry)
        transaction.set(ref, data)
        return entry
    }

    private fun deleteFromDb(pageId: String, entry: Entry, transaction: Transaction): Entry {
        val ref = FirebaseHelper.getEntryReference(pageId, entry.entryId)
        checkIfEntryExists(transaction, pageId, entry.entryId)
        transaction.delete(ref)
        return entry
    }

    private suspend fun get(pageId: String, query: String, sf: String?): List<Entry> {
        val startAfter = sf ?: ""
        val ss = FirebaseHelper.getEntryCollection(pageId)
            .orderBy("entryId").startAfter(startAfter).limit(10).get().await()
        return ss?.documents?.map { ds ->
            val obj = ds.toObject(RemoteEntityEntry::class.java)!!
            mapper.mapFromEntity(obj)
        } ?: listOf()
    }

    private suspend fun get(pageId: String, entryId: String): Entry {
        val ds = FirebaseHelper.getEntryReference(pageId, entryId).get().await()
        if (!ds.exists()) throw EntryNotFoundException()
        val obj = ds.toObject(RemoteEntityEntry::class.java)!!
        return mapper.mapFromEntity(obj)
    }

    private suspend fun checkIfPageExists(pageId: String) {
        val obj = FirebaseHelper.getPageReference(pageId).get().await()
        if (!obj.exists()) {
            throw PageNotFoundException()
        }
    }

    private fun checkIfEntryExists(
        transaction: Transaction,
        pageId: String,
        entryId: String
    ) {
        val ref = FirebaseHelper.getEntryReference(pageId, entryId)
        val obj = transaction.get(ref)
        if (!obj.exists()) {
            throw EntryNotFoundException()
        }
    }
}