package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface NotebookService {

    suspend fun getNotebooks(): Flow<List<Notebook>>

    suspend fun insertNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>>

    suspend fun insertNotebook(notebook: Notebook): DataState<Notebook>

    suspend fun updateNotebooks(updatedNotebooks: List<Notebook>): DataState<List<Notebook>>

    suspend fun updateNotebook(notebook: Notebook): DataState<Notebook>

    suspend fun deleteNotebook(notebookId: String): DataState<String>

    suspend fun deleteNotebooks(notebookIds: List<String>): DataState<String>

}