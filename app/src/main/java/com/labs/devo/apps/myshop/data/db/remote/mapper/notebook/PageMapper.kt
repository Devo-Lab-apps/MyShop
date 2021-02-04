package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityPage
import com.labs.devo.apps.myshop.data.models.notebook.Page

class PageMapper : EntityMapper<EntityPage, Page> {

    fun entityListToPageList(entities: List<EntityPage>): List<Page> {
        val list: ArrayList<Page> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun pageListToEntityList(pages: List<Page>): List<EntityPage> {
        val entities: ArrayList<EntityPage> = ArrayList()
        for (page in pages) {
            entities.add(mapToEntity(page))
        }
        return entities
    }

    override fun mapFromEntity(entity: EntityPage): Page {
        return Page(
            pageId = entity.pageId,
            pageName = entity.pageName,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }

    override fun mapToEntity(model: Page): EntityPage {
        return EntityPage(
            pageId = model.pageId,
            pageName = model.pageName,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt
        )
    }

}