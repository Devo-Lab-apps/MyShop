package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.PageDao
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalPageServiceImpl
@Inject constructor(
    val dao: PageDao
) : LocalPageService {

    override fun getPages(
        notebookId: String,
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Page> {
        var finalOrderBy = orderBy
        if (orderBy.isEmpty()) {
            finalOrderBy = Page::pageName.name
        }
        return dao.getPages(notebookId, "%$searchQuery%", finalOrderBy)
    }

    override suspend fun getPage(pageId: String): Page? {
        return AsyncHelper.runAsync {
            dao.getPage(pageId)
        }
    }

    override suspend fun insertPages(pages: List<Page>) {
        return AsyncHelper.runAsync {
            dao.insertPages(pages)
        }
    }

    override suspend fun insertPage(page: Page) {
        return AsyncHelper.runAsync {
            dao.insertPage(page)
        }
    }

    override suspend fun updatePages(pages: List<Page>) {
        return AsyncHelper.runAsync {
            dao.updatePages(pages)
        }
    }

    override suspend fun updatePage(page: Page) {
        return AsyncHelper.runAsync {
            dao.updatePage(page)
        }
    }

    override suspend fun deletePage(page: Page) {
        return AsyncHelper.runAsync {
            dao.deletePage(page)
        }
    }

    override suspend fun deletePages(pages: List<Page>) {
        return AsyncHelper.runAsync {
            dao.deletePages(pages)
        }
    }

    override suspend fun deletePages(notebookId: String) {
        return AsyncHelper.runAsync {
            dao.deletePages(notebookId)
        }
    }

    override suspend fun deletePages() {
        AsyncHelper.runAsync {
            dao.deleteAll()
        }
    }
}