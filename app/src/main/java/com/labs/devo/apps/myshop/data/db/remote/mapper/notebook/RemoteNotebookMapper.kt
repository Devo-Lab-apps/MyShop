package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook

class RemoteNotebookMapper: EntityMapper<RemoteEntityNotebook, Notebook> {

    fun entityListToNotebookList(remoteEntities: List<RemoteEntityNotebook>): List<Notebook> {
        val list: ArrayList<Notebook> = ArrayList()
        for (entity in remoteEntities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun notebookListToEntityList(notebooks: List<Notebook>): List<RemoteEntityNotebook> {
        val remoteEntities: ArrayList<RemoteEntityNotebook> = ArrayList()
        for (account in notebooks) {
            remoteEntities.add(mapToEntity(account))
        }
        return remoteEntities
    }

    override fun mapFromEntity(remoteEntity: RemoteEntityNotebook): Notebook {
        return Notebook(
            remoteEntity.notebookId,
            remoteEntity.notebookName,
            remoteEntity.createdAt,
            remoteEntity.modifiedAt,
            remoteEntity.pages
        )
    }

    override fun mapToEntity(model: Notebook): RemoteEntityNotebook {
        return RemoteEntityNotebook(
            model.notebookId,
            model.notebookName,
            model.createdAt,
            model.modifiedAt,
            model.pages.toMutableList()
        )
    }

}