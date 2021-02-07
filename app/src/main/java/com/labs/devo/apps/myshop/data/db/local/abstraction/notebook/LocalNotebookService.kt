package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Notebook

interface LocalNotebookService {

    suspend fun getNotebooks(): List<Notebook>?

    suspend fun getNotebook(notebookId: String): Notebook?

    suspend fun insertNotebooks(notebooks: List<Notebook>): List<Notebook>

    suspend fun insertNotebook(notebook: Notebook): Notebook

    suspend fun updateNotebooks(notebooks: List<Notebook>): List<Notebook>

    suspend fun updateNotebook(notebook: Notebook): Notebook

    suspend fun deleteNotebook(notebook: Notebook)

    suspend fun deleteNotebooks(notebooks: List<Notebook>)

}