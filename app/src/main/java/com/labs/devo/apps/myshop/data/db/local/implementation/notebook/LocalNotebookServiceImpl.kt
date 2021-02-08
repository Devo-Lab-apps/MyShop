package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalNotebookMapper
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import javax.inject.Inject

class LocalNotebookServiceImpl @Inject
constructor(
    val mapper: LocalNotebookMapper,
    val dao: NotebookDao
) :
    LocalNotebookService {

    override suspend fun getNotebooks(): List<Notebook> {
        return mapper.entityListToNotebookList(dao.getNotebooks())
    }

    override suspend fun getNotebook(notebookId: String): Notebook {
        return mapper.mapFromEntity(dao.getNotebook(notebookId))
    }

    override suspend fun insertNotebooks(notebooks: List<Notebook>): List<Notebook> {
        dao.insertNotebooks(mapper.notebookListToEntityList(notebooks))
        return notebooks
    }

    override suspend fun insertNotebook(notebook: Notebook): Notebook {
        dao.insertNotebook(mapper.mapToEntity(notebook))
        return notebook
    }

    override suspend fun updateNotebooks(notebooks: List<Notebook>): List<Notebook> {
        dao.updateNotebooks(mapper.notebookListToEntityList(notebooks))
        return notebooks
    }

    override suspend fun updateNotebook(notebook: Notebook): Notebook {
        dao.updateNotebook(mapper.mapToEntity(notebook))
        return notebook
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        dao.deleteNotebook(mapper.mapToEntity(notebook))
    }

    override suspend fun deleteNotebooks(notebooks: List<Notebook>) {
        dao.deleteNotebooks(mapper.notebookListToEntityList(notebooks))
    }

}