package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface RemoteNotebookService {

    suspend fun getNotebooks(): List<Notebook>

    suspend fun insertNotebook(notebook: Notebook): Notebook

    suspend fun updateNotebook(notebook: Notebook): Notebook

    suspend fun deleteNotebook(notebook: Notebook)

}