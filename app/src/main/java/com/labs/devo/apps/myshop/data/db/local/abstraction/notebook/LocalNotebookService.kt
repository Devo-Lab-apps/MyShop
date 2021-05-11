package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Notebook

interface LocalNotebookService {

    suspend fun getNotebooks(): List<Notebook>?

    suspend fun getNotebook(notebookId: String): Notebook?

    suspend fun insertNotebooks(notebooks: List<Notebook>)

    suspend fun insertNotebook(notebook: Notebook)

    suspend fun updateNotebook(notebook: Notebook)

    suspend fun deleteNotebook(notebook: Notebook)

    suspend fun deleteNotebooks()

    suspend fun getLastFetchedNotebook(): Notebook?

}