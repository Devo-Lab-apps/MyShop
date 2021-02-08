package com.labs.devo.apps.myshop.data.db.local.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityPage
import com.labs.devo.apps.myshop.data.models.notebook.Page

class LocalPageMapper : EntityMapper<LocalEntityPage, Page> {

    fun entityListToPageList(entities: List<LocalEntityPage>): List<Page> {
        val list: ArrayList<Page> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun pageListToEntityList(pages: List<Page>): List<LocalEntityPage> {
        val entities: ArrayList<LocalEntityPage> = ArrayList()
        for (page in pages) {
            entities.add(mapToEntity(page))
        }
        return entities
    }

    override fun mapFromEntity(entity: LocalEntityPage): Page {
        return Page(
            pageId = entity.pageId,
            pageName = entity.pageName,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
    }

    override fun mapToEntity(model: Page): LocalEntityPage {
        return LocalEntityPage(
            pageId = model.pageId,
            pageName = model.pageName,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt
        )
    }
}