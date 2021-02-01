package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import androidx.lifecycle.asLiveData
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.NotebookService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.NotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotebookServiceFirebaseImpl @Inject constructor(
    private val mapper: NotebookMapper
) : NotebookService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getNotebooks(): Flow<List<Notebook>> {
        return channelFlow {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")

            val querySnapshot = FirebaseHelper.getNotebookCollection(user.accountId)
                .get().await()

            offer(querySnapshot.documents.map { doc ->
                mapper.mapFromEntity(doc.toObject(EntityNotebook::class.java)!!)
            })
        }
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> {
        val existingNotebooks = getNotebooks().asLiveData().value

        if (existingNotebooks?.size == 3) {
            return DataState.message("You can't have more than 3 notebooks per account.")
        }
        val insertedNotebooks = mutableListOf<Notebook>()
        return try {
            FirebaseHelper.runWriteBatch {
                notebooks.forEach { notebook ->
                    insertedNotebooks.add(insertInDb(notebook))
                }
            }
            DataState.data(data = insertedNotebooks)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): DataState<Notebook> {
        val existingNotebooks = getNotebooks().first()

        if (existingNotebooks.size >= 3) {
            return DataState.message("You can't have more than 3 notebooks per account.")
        }
        var updatedNotebook: Notebook? = null
        return try {
            FirebaseHelper.runWriteBatch {
                updatedNotebook = insertInDb(notebook)
            }
            DataState.data(data = updatedNotebook)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun updateNotebooks(updatedNotebooks: List<Notebook>): DataState<List<Notebook>> {
        val updatedData = mutableListOf<Notebook>()
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        updatedNotebooks.forEach { notebook ->
            val existing =
                FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId)
                    .get().await()
            if (!existing?.exists()!!) {
                return DataState.message("The notebook is not present and is deleted by another user.")
            }
        }
        return try {
            FirebaseHelper.runUpdateBatch {
                updatedNotebooks.forEach { notebook ->
                    updatedData.add(updateInDb(notebook))
                }
            }
            DataState.data(updatedData)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun updateNotebook(notebook: Notebook): DataState<Notebook> {
        return try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            val existing =
                FirebaseHelper.getNotebookReference(user.accountId, notebook.notebookId).get()
                    .await()
            if (!existing.exists()) {
                return DataState.message("The notebook is not present and maybe deleted by another user.")
            }
            DataState.data(updateInDb(notebook))
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun deleteNotebook(notebookId: String): DataState<String> {
        return try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            val existing =
                FirebaseHelper.getNotebookReference(user.accountId, notebookId).get().await()
            if (!existing.exists()) {
                return DataState.data("The notebook is already deleted by another user.")
            }
            deleteFromDb(notebookId)
            DataState.data("Notebook deleted.")
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun deleteNotebooks(notebookIds: List<String>): DataState<String> {
        try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            notebookIds.forEach { notebookId ->
                val existing =
                    FirebaseHelper.getNotebookReference(user.accountId, notebookId).get().await()
                if (!existing.exists()) {
                    return DataState.data("The notebook is already deleted by another user.")
                }
            }
            FirebaseHelper.runWriteBatch {
                notebookIds.forEach { notebookId -> deleteFromDb(notebookId) }
            }
            return DataState.message("Notebooks deleted.")
        } catch (ex: java.lang.Exception) {
            return DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }

    }

    private fun checkIfForeign(notebook: Notebook) {
        if (notebook.notebookName == "Foreign" || notebook.notebookId == "foreign") {
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
        val data = mapper.mapToEntity(notebook.copy(
            notebookId = notebookId
        ))
        FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .set(data)
        return mapper.mapFromEntity(data)
    }


    private fun updateInDb(notebook: Notebook): Notebook {
        checkIfForeign(notebook)
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        if (notebook.notebookId.isBlank()) {
            throw java.lang.Exception("Invalid notebook id passed.")
        }
        val data = mapper.mapToEntity(
            notebook.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        FirebaseHelper.getNotebookReference(user.accountId, data.notebookId)
            .set(data)
        return mapper.mapFromEntity(data)
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