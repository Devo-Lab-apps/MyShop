package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalNotebookMapper
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalNotebookServiceImpl @Inject
constructor(
    val mapper: LocalNotebookMapper,
    val dao: NotebookDao
) :
    LocalNotebookService {

    override suspend fun getNotebooks(): List<Notebook> {
        return AsyncHelper.runAsync {
            mapper.entityListToNotebookList(dao.getNotebooks())
        }
    }

    override suspend fun getNotebook(notebookId: String): Notebook {
        return AsyncHelper.runAsync { mapper.mapFromEntity(dao.getNotebook(notebookId)) }
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>): List<Notebook> {
        return AsyncHelper.runAsync {
            dao.insertNotebooks(mapper.notebookListToEntityList(notebooks))
            notebooks
        }
    }

    override suspend fun insertNotebook(notebook: Notebook): Notebook {
        return AsyncHelper.runAsync {
            dao.insertNotebook(mapper.mapToEntity(notebook))
            notebook
        }
    }

    override suspend fun updateNotebooks(notebooks: List<Notebook>): List<Notebook> {
        return AsyncHelper.runAsync {
            dao.updateNotebooks(mapper.notebookListToEntityList(notebooks))
            notebooks
        }
    }

    override suspend fun updateNotebook(notebook: Notebook): Notebook {
        return AsyncHelper.runAsync {
            dao.updateNotebook(mapper.mapToEntity(notebook))
            notebook
        }
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        return AsyncHelper.runAsync {
            dao.deleteNotebook(mapper.mapToEntity(notebook))
        }
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>) {
        return AsyncHelper.runAsync {
            dao.deleteNotebooks(mapper.notebookListToEntityList(notebooks))
        }
    }

    override suspend fun deleteNotebooks() {
        return AsyncHelper.runAsync {
            dao.deleteNotebooks()
        }
    }

}