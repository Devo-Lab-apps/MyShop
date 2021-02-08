package com.labs.devo.apps.myshop.business.notebook.abstraction

import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {

    suspend fun getNotebooks(): Flow<DataState<List<Notebook>>>

    suspend fun insertNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>>

    suspend fun insertNotebook(notebook: Notebook): DataState<Notebook>

    suspend fun updateNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>>

    suspend fun updateNotebook(notebook: Notebook): DataState<Notebook>

    suspend fun deleteNotebook(notebook: Notebook): DataState<Notebook>

    suspend fun deleteNotebooks(notebooks: List<Notebook>): DataState<List<Notebook>>

}