package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.util.exceptions.NotebookLimitExceededException
import com.labs.devo.apps.myshop.util.exceptions.NotebookNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteNotebookServiceFirebaseImpl @Inject constructor(
    private val mapperRemote: RemoteNotebookMapper
) : RemoteNotebookService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getNotebooks(): List<Notebook> {
        val user = UserManager.user ?: throw UserNotInitializedException()

        val querySnapshot = FirebaseHelper.getNotebookCollection(user.accountId)
            .get().await()

        return (querySnapshot.documents.map { doc ->
            mapperRemote.mapFromEntity(doc.toObject(RemoteEntityNotebook::class.java)!!)
        })
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>): List<Notebook> {
        val existingNotebooks = getNotebooks()

        if (existingNotebooks.size == 3) {
            throw NotebookLimitExceededException("You can't have more than 3 notebooks per account.")
        }
        val insertedNotebooks = mutableListOf<Notebook>()
        FirebaseHelper.runWriteBatch {
            notebooks.forEach { notebook ->
                insertedNotebooks.add(insertInDb(notebook))
            }
        }
        return insertedNotebooks
    }

    override suspend fun insertNotebook(notebook: Notebook): Notebook {
        val existingNotebooks = getNotebooks()

        if (existingNotebooks.size >= 3) {
            throw NotebookLimitExceededException("You can't have more than 3 notebooks per account.")
        }
        var insertedNotebook = notebook.copy()
        FirebaseHelper.runWriteBatch {
            insertedNotebook = insertInDb(notebook)
        }
        return insertedNotebook
    }

    override suspend fun updateNotebooks(notebooks: List<Notebook>): List<Notebook> {
        val updatedNotebooks = mutableListOf<Notebook>()
        val user = UserManager.user ?: throw UserNotInitializedException()

        FirebaseHelper.runTransaction { transaction ->
            updatedNotebooks.forEach { notebook ->
                val ref = FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId)
                val existing = transaction.get(ref)
                if (!existing.exists()) {
                    throw NotebookNotFoundException()
                }
            }
            updatedNotebooks.forEach { notebook ->
                updatedNotebooks.add(updateInDb(notebook, transaction))
            }
        }
        return updatedNotebooks
    }

    override suspend fun updateNotebook(notebook: Notebook): Notebook {
        val user = UserManager.user ?: throw UserNotInitializedException()
        var updatedNotebook = notebook.copy()
        FirebaseHelper.runTransaction { transaction ->
            val ref = FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw NotebookNotFoundException()
            }
            updatedNotebook = updateInDb(notebook, transaction)
        }
        return updatedNotebook
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        val notebookId = notebook.notebookId
        val user = UserManager.user ?: throw UserNotInitializedException()
        FirebaseHelper.runTransaction { transaction ->
            val ref = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw NotebookNotFoundException()
            }
            deleteFromDb(notebookId, transaction)
        }
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>) {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val notebookIds = notebooks.map { it.notebookId }
        FirebaseHelper.runTransaction { transaction ->
            notebookIds.forEach { notebookId ->
                val ref =
                    FirebaseHelper.getNotebookReference(user.accountId, notebookId)
                val existing = transaction.get(ref)
                if (!existing.exists()) {
                    throw NotebookNotFoundException()
                }
            }
            notebookIds.forEach { notebookId -> deleteFromDb(notebookId, transaction) }
        }

    }

    private fun checkIfForeign(notebook: Notebook) {
        if (notebook.notebookName == FirebaseConstants.foreignNotebookName || notebook.notebookId == FirebaseConstants.foreignNotebookKey) {
            throw java.lang.Exception("You can't perform any operation on foreign notebook")
        }
    }

    private fun checkIfForeign(notebookId: String) {
        if (notebookId == "foreign") {
            throw java.lang.Exception("You can't perform any operation on foreign notebook")
        }
    }


    private fun insertInDb(notebook: Notebook): Notebook {
        checkIfForeign(notebook)
        val user = UserManager.user ?: throw UserNotInitializedException()
        val notebookId = FirebaseHelper.getNotebookReference(user.accountId).id
        val data = mapperRemote.mapToEntity(
            notebook.copy(
                notebookId = notebookId
            )
        )
        FirebaseHelper.getNotebookReference(user.accountId, notebookId).set(data)
        return mapperRemote.mapFromEntity(data)
    }


    private fun updateInDb(notebook: Notebook, transaction: Transaction): Notebook {
        checkIfForeign(notebook)
        val user = UserManager.user ?: throw UserNotInitializedException()
        if (notebook.notebookId.isBlank()) {
            throw java.lang.Exception("Invalid notebook id passed.")
        }
        val data = mapperRemote.mapToEntity(
            notebook.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        transaction.set(
            FirebaseHelper.getNotebookReference(user.accountId, data.notebookId), data
        )
        return mapperRemote.mapFromEntity(data)
    }

    private fun deleteFromDb(notebookId: String, transaction: Transaction) {
        checkIfForeign(notebookId)
        val user = UserManager.user ?: throw Exception("User not initialized")
        if (notebookId.isBlank()) {
            throw java.lang.Exception("Invalid notebook id passed.")
        }
        transaction.delete(FirebaseHelper.getNotebookReference(user.accountId, notebookId))
    }


}