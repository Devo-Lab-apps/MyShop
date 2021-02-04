package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotebookRepositoryImpl @Inject constructor(
    val remoteNotebookService: RemoteNotebookService
) : NotebookRepository {


    override suspend fun getNotebooks(): Flow<List<Notebook>> = remoteNotebookService.getNotebooks()

    override suspend fun insertNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> =
        remoteNotebookService.insertNotebooks(notebooks)

    override suspend fun insertNotebook(notebook: Notebook): DataState<Notebook> =
        remoteNotebookService.insertNotebook(notebook)

    override suspend fun updateNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>> =
        remoteNotebookService.updateNotebooks(notebooks)

    override suspend fun updateNotebook(notebook: Notebook): DataState<Notebook> =
        remoteNotebookService.updateNotebook(notebook)

    override suspend fun deleteNotebook(notebookId: String): DataState<String> =
        remoteNotebookService.deleteNotebook(notebookId)

    override suspend fun deleteNotebooks(notebookIds: List<String>): DataState<String> =
        remoteNotebookService.deleteNotebooks(notebookIds)
}