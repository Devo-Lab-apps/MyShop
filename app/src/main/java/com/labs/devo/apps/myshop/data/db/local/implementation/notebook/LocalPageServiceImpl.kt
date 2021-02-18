package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalPageServiceImpl
@Inject constructor(
    val mapper: LocalPageMapper,
    val dao: PageDao
) : LocalPageService {

    override suspend fun getPages(notebookId: String, searchQuery: String): List<Page> {
        return AsyncHelper.runAsync {
            mapper.entityListToPageList(dao.getPages(notebookId, searchQuery))
        }
    }

    override suspend fun insertPages(pages: List<Page>): List<Page> {
        return AsyncHelper.runAsync {
            dao.insertPages(mapper.pageListToEntityList(pages))
            pages
        }
    }

    override suspend fun insertPage(page: Page): Page {
        return AsyncHelper.runAsync {
            dao.insertPage(mapper.mapToEntity(page))
            page
        }
    }

    override suspend fun updatePages(pages: List<Page>): List<Page> {
        return AsyncHelper.runAsync {
            dao.updatePages(mapper.pageListToEntityList(pages))
            pages
        }
    }

    override suspend fun updatePage(page: Page): Page {
        return AsyncHelper.runAsync {
            dao.updatePage(mapper.mapToEntity(page))
            page
        }
    }

    override suspend fun deletePage(page: Page) {
        return AsyncHelper.runAsync {
            dao.deletePage(mapper.mapToEntity(page))
        }
    }

    override suspend fun deletePages(pages: List<Page>) {
        return AsyncHelper.runAsync {
            dao.deletePages(mapper.pageListToEntityList(pages))
        }
    }

    override suspend fun deletePages(notebookId: String) {
        return AsyncHelper.runAsync {
            dao.deletePages(notebookId)
        }
    }

}