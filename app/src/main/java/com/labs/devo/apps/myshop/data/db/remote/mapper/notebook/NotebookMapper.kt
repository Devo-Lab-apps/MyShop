package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook

class NotebookMapper: EntityMapper<EntityNotebook, Notebook> {

    fun entityListToNotebookList(entities: List<EntityNotebook>): List<Notebook> {
        val list: ArrayList<Notebook> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun notebookListToEntityList(accounts: List<Notebook>): List<EntityNotebook> {
        val entities: ArrayList<EntityNotebook> = ArrayList()
        for (account in accounts) {
            entities.add(mapToEntity(account))
        }
        return entities
    }

    override fun mapFromEntity(entity: EntityNotebook): Notebook {
        return Notebook(
            entity.notebookId,
            entity.notebookName,
            entity.createdAt,
            entity.modifiedAt,
            entity.pages
        )
    }

    override fun mapToEntity(model: Notebook): EntityNotebook {
        return EntityNotebook(
            model.notebookId,
            model.notebookName,
            model.createdAt,
            model.modifiedAt,
            model.pages
        )
    }

}