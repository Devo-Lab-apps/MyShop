package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.const.AppConstants.ONE_DAY_MILLIS
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.util.exceptions.NotebookNotFoundException
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotebookRepositoryImpl @Inject constructor(
    private val localNotebookService: LocalNotebookService,
    private val remoteNotebookService: RemoteNotebookService
) : NotebookRepository {


    override suspend fun getNotebooks(): Flow<DataState<List<Notebook>>> = flow {
        emit(DataState.loading<List<Notebook>>(true))
        try {
            checkPermissions(Permissions.GET_NOTEBOOK)
            var notebooks = localNotebookService.getNotebooks()
            if (notebooks.isNullOrEmpty()) {
                val remoteNotebooks = remoteNotebookService.getNotebooks()
                localNotebookService.insertNotebooks(remoteNotebooks)
                notebooks = remoteNotebooks
            }
            emit(DataState.data(notebooks))
            val lastFetchedNotebook = localNotebookService.getLastFetchedNotebook()
            lastFetchedNotebook?.let { notebook ->
                if (notebook.fetchedAt < System.currentTimeMillis() - ONE_DAY_MILLIS) emit(
                    syncNotebooks()
                )
            }
        } catch (ex: Exception) {
            emit(
                DataState.message<List<Notebook>>(
                    ex.message ?: "An unknown error occurred. Please retry later."
                )
            )
        }
    }

    override suspend fun getNotebook(notebookId: String): Notebook {
        try {
            checkPermissions(Permissions.GET_NOTEBOOK)
            val localNotebook = localNotebookService.getNotebook(notebookId)
            return localNotebook ?: throw NotebookNotFoundException()
        } catch (ex: java.lang.Exception) {
            throw NotebookNotFoundException()
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): DataState<Notebook> {

        return try {
            checkPermissions(Permissions.CREATE_NOTEBOOK)
            val insertNotebook = remoteNotebookService.insertNotebook(notebook)
            localNotebookService.insertNotebook(insertNotebook)
            DataState.data(insertNotebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateNotebook(notebook: Notebook): DataState<Notebook> {

        return try {
            checkPermissions(Permissions.CREATE_NOTEBOOK)
            val updatedNotebook = remoteNotebookService.updateNotebook(notebook)
            localNotebookService.updateNotebook(updatedNotebook)
            DataState.data(updatedNotebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteNotebook(notebook: Notebook): DataState<Notebook> {
        return try {
            checkPermissions(Permissions.DELETE_NOTEBOOK)
            remoteNotebookService.deleteNotebook(notebook)
            localNotebookService.deleteNotebook(notebook)
            DataState.data(notebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteNotebooks() {
        localNotebookService.deleteNotebooks()
    }

    override suspend fun syncNotebooks(): DataState<List<Notebook>> {
        return try {
            localNotebookService.deleteNotebooks()
            val notebooks = remoteNotebookService.getNotebooks()
            localNotebookService.insertNotebooks(notebooks)
            DataState.data(notebooks)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}