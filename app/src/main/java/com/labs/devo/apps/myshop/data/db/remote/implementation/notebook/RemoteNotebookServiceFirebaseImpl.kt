package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

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
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")

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
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")

        FirebaseHelper.runUpdateBatch {
            updatedNotebooks.forEach { notebook ->
                val existing =
                    FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId)
                        .get().result
                if (existing?.exists() == false) {
                    throw NotebookNotFoundException("The notebook is not present and maybe deleted by another user.")
                }
            }
            updatedNotebooks.forEach { notebook ->
                updatedNotebooks.add(updateInDb(notebook))
            }
        }
        return updatedNotebooks
    }

    override suspend fun updateNotebook(notebook: Notebook): Notebook {
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        var updatedNotebook = notebook.copy()
        FirebaseHelper.runUpdateBatch {
            val existing =
                FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId)
                    .get().result
            if (existing?.exists() == false) {
                throw NotebookNotFoundException("The notebook is not present and maybe deleted by another user.")
            }
            updatedNotebook = updateInDb(notebook)
        }
        return updatedNotebook
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        val notebookId = notebook.notebookId
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        FirebaseHelper.runUpdateBatch {
            val existing =
                FirebaseHelper.getNotebookReference(user.accountId, notebookId).get().result
            if (existing?.exists() == false) {
                throw NotebookNotFoundException("The notebook is not present and maybe deleted by another user.")
            }
            deleteFromDb(notebookId)
        }
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>) {
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        val notebookIds = notebooks.map { it.notebookId }
        FirebaseHelper.runWriteBatch {
            notebookIds.forEach { notebookId ->
                val existing =
                    FirebaseHelper.getNotebookReference(user.accountId, notebookId).get().result
                if (false == existing?.exists()) {
                    throw NotebookNotFoundException("The notebook is not present and maybe deleted by another user.")
                }
            }
            notebookIds.forEach { notebookId -> deleteFromDb(notebookId) }
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
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        val notebookId = FirebaseHelper.getNotebookReference(user.accountId).id
        val data = mapperRemote.mapToEntity(
            notebook.copy(
                notebookId = notebookId
            )
        )
        FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .set(data)
        return mapperRemote.mapFromEntity(data)
    }


    private fun updateInDb(notebook: Notebook): Notebook {
        checkIfForeign(notebook)
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        if (notebook.notebookId.isBlank()) {
            throw java.lang.Exception("Invalid notebook id passed.")
        }
        val data = mapperRemote.mapToEntity(
            notebook.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        FirebaseHelper.getNotebookReference(user.accountId, data.notebookId)
            .set(data)
        return mapperRemote.mapFromEntity(data)
    }

    private fun deleteFromDb(notebookId: String) {
        checkIfForeign(notebookId)
        val user = UserManager.user ?: throw Exception("User not initialized")
        if (notebookId.isBlank()) {
            throw java.lang.Exception("Invalid notebook id passed.")
        }
        FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .delete()
    }


}