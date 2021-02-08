package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.models.notebook.Page
import javax.inject.Inject

class LocalPageServiceImpl
@Inject constructor(
    val mapper: LocalPageMapper,
    val dao: PageDao
) : LocalPageService {

    override suspend fun getPages(notebookId: String): List<Page> {
        return mapper.entityListToPageList(dao.getPages(notebookId))
    }

    override suspend fun insertPages(pages: List<Page>): List<Page> {
        dao.insertPages(mapper.pageListToEntityList(pages))
        return pages
    }

    override suspend fun insertPage(page: Page): Page {
        dao.insertPage(mapper.mapToEntity(page))
        return page
    }

    override suspend fun updatePages(pages: List<Page>): List<Page> {
        dao.updatePages(mapper.pageListToEntityList(pages))
        return pages
    }

    override suspend fun updatePage(page: Page): Page {
        dao.updatePage(mapper.mapToEntity(page))
        return page
    }

    override suspend fun deletePage(page: Page) {
        dao.deletePage(mapper.mapToEntity(page))
    }

    override suspend fun deletePages(pages: List<Page>) {
        dao.deletePages(mapper.pageListToEntityList(pages))
    }

}