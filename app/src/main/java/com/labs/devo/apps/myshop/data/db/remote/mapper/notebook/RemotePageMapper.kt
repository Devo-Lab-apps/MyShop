package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityPage
import com.labs.devo.apps.myshop.data.models.notebook.Page

class RemotePageMapper : EntityMapper<RemoteEntityPage, Page> {

    fun entityListToPageList(remoteEntities: List<RemoteEntityPage>): List<Page> {
        val list: ArrayList<Page> = ArrayList()
        for (entity in remoteEntities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun pageListToEntityList(pages: List<Page>): List<RemoteEntityPage> {
        val remoteEntities: ArrayList<RemoteEntityPage> = ArrayList()
        for (page in pages) {
            remoteEntities.add(mapToEntity(page))
        }
        return remoteEntities
    }

    override fun mapFromEntity(remoteEntity: RemoteEntityPage): Page {
        return Page(
            creatorAccountId = remoteEntity.creatorAccountId,
            consumerAccountId = remoteEntity.consumerAccountId,
            creatorNotebookId = remoteEntity.creatorNotebookId,
            consumerNotebookId = remoteEntity.consumerNotebookId,
            pageId = remoteEntity.pageId,
            pageName = remoteEntity.pageName,
            createdAt = remoteEntity.createdAt,
            modifiedAt = remoteEntity.modifiedAt
        )
    }

    override fun mapToEntity(model: Page): RemoteEntityPage {
        return RemoteEntityPage(
            creatorAccountId = model.creatorAccountId,
            consumerAccountId = model.consumerAccountId,
            creatorNotebookId = model.creatorNotebookId,
            consumerNotebookId = model.consumerNotebookId,
            pageId = model.pageId,
            pageName = model.pageName,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt
        )
    }

}