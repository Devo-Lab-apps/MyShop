package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotebookRepositoryImpl @Inject constructor(
    val localNotebookService: LocalNotebookService,
    val remoteNotebookService: RemoteNotebookService
) : NotebookRepository {


    override suspend fun getNotebooks(): Flow<DataState<List<Notebook>>> = flow {
        emit(DataState.loading<List<Notebook>>(true))
        try {
            var localNotebooks = localNotebookService.getNotebooks()
            if (localNotebooks.isNullOrEmpty()) {
                val notebooks = remoteNotebookService.getNotebooks()
                localNotebooks = localNotebookService.insertNotebooks(notebooks)
            }
            emit(DataState.data(localNotebooks))
        } catch (ex: Exception) {
            emit(
                DataState.message<List<Notebook>>(
                    ex.message ?: "An unknown error occurred. Please retry later."
                )
            )
        }
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> {
        return try {
            val insertedNotebooks = remoteNotebookService.insertNotebooks(notebooks)
            val localInsertedNotebooks = localNotebookService.insertNotebooks(insertedNotebooks)
            DataState.data(localInsertedNotebooks)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): DataState<Notebook> {
        return try {
            val insertNotebook = remoteNotebookService.insertNotebook(notebook)
            val localInsertedNotebook = localNotebookService.insertNotebook(insertNotebook)
            DataState.data(localInsertedNotebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> {
        return try {
            val updatedNotebooks = remoteNotebookService.updateNotebooks(notebooks)
            val localUpdated = localNotebookService.updateNotebooks(updatedNotebooks)
            DataState.data(localUpdated)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }

    }

    override suspend fun updateNotebook(notebook: Notebook): DataState<Notebook> {
        return try {
            val updatedNotebook = remoteNotebookService.updateNotebook(notebook)
            val localUpdatedNotebook = localNotebookService.updateNotebook(updatedNotebook)
            DataState.data(localUpdatedNotebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteNotebook(notebook: Notebook): DataState<Notebook> {
        return try {
            remoteNotebookService.deleteNotebook(notebook)
            localNotebookService.deleteNotebook(notebook)
            DataState.data(notebook)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> {
        return try {
            remoteNotebookService.deleteNotebooks(notebooks)
            localNotebookService.deleteNotebooks(notebooks)
            DataState.data(notebooks)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}