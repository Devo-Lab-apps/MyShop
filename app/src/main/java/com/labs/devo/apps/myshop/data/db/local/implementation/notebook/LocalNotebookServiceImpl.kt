package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.NotebookDao
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalNotebookServiceImpl @Inject
constructor(
    val dao: NotebookDao
) :
    LocalNotebookService {

    override suspend fun getNotebooks(): List<Notebook> {
        return AsyncHelper.runAsync {
            dao.getNotebooks()
        }
    }

    override suspend fun getNotebook(notebookId: String): Notebook {
        return AsyncHelper.runAsync { dao.getNotebook(notebookId) }
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>) {
        return AsyncHelper.runAsync {
            dao.insertNotebooks(notebooks)
        }
    }

    override suspend fun insertNotebook(notebook: Notebook) {
        return AsyncHelper.runAsync {
            dao.insertNotebook(notebook)
        }
    }

    override suspend fun updateNotebooks(notebooks: List<Notebook>) {
        return AsyncHelper.runAsync {
            dao.updateNotebooks(notebooks)
        }
    }

    override suspend fun updateNotebook(notebook: Notebook) {
        return AsyncHelper.runAsync {
            dao.updateNotebook(notebook)
        }
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        return AsyncHelper.runAsync {
            dao.deleteNotebook(notebook)
        }
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>) {
        return AsyncHelper.runAsync {
            dao.deleteNotebooks(notebooks)
        }
    }

    override suspend fun deleteNotebooks() {
        return AsyncHelper.runAsync {
            dao.deleteNotebooks()
        }
    }

    override suspend fun getLastFetchedNotebook(): Notebook? {
        return AsyncHelper.runAsync {
            dao.getLastFetchedNotebook()
        }
    }

}