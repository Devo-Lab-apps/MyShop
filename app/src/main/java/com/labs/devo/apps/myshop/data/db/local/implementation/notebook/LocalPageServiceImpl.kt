package com.labs.devo.apps.myshop.data.db.local.implementation.notebook

import androidx.sqlite.db.SimpleSQLiteQuery
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import com.labs.devo.apps.myshop.view.util.QueryParams
import javax.inject.Inject

class LocalPageServiceImpl
@Inject constructor(
    val mapper: LocalPageMapper,
    val dao: PageDao
) : LocalPageService {

    override suspend fun getPages(notebookId: String, queryParams: QueryParams): List<Page> {
        return AsyncHelper.runAsync {
            var query = StringBuilder("SELECT * FROM Page WHERE creatorNotebookId = '$notebookId'")
            if (queryParams.whereQuery.isNotEmpty()) {
                query.append(" AND ${queryParams.buildWhereQuery()}")
            }
            if (queryParams.orderBy.isNotEmpty()) {
                query.append(" order by ${queryParams.buildOrderByQuery()}")
            }
            if (queryParams.limit != Int.MAX_VALUE) {
                query.append(" limit ${queryParams.limit}")
            }
            if (queryParams.offset != 0) {
                query.append(" offset ${queryParams.offset}")
            }
            val rawQuery = SimpleSQLiteQuery(
                query.toString()
            )
            mapper.entityListToPageList(
                dao.getPages(rawQuery)
            )
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