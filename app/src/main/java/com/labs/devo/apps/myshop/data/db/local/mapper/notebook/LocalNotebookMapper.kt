package com.labs.devo.apps.myshop.data.db.local.mapper.notebook

import com.labs.devo.apps.myshop.view.util.EntityMapper
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook

class LocalNotebookMapper : EntityMapper<LocalEntityNotebook, Notebook> {

    fun entityListToNotebookList(entities: List<LocalEntityNotebook>): List<Notebook> {
        val list: ArrayList<Notebook> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun notebookListToEntityList(notebooks: List<Notebook>): List<LocalEntityNotebook> {
        val entities: ArrayList<LocalEntityNotebook> = ArrayList()
        for (notebook in notebooks) {
            entities.add(mapToEntity(notebook))
        }
        return entities
    }

    override fun mapFromEntity(entity: LocalEntityNotebook): Notebook {
        return Notebook(
            notebookId = entity.notebookId,
            notebookName = entity.notebookName,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            pages = entity.pages
        )
    }

    override fun mapToEntity(model: Notebook): LocalEntityNotebook {
        return LocalEntityNotebook(
            notebookId = model.notebookId,
            notebookName = model.notebookName,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt,
            pages = model.pages
        )
    }
}