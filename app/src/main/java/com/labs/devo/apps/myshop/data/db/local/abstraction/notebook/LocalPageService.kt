package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.QueryParams

interface LocalPageService {


    suspend fun getPages(notebookId: String, queryParams: QueryParams): List<Page>?

    suspend fun insertPages(pages: List<Page>): List<Page>

    suspend fun insertPage(page: Page): Page

    suspend fun updatePages(pages: List<Page>): List<Page>

    suspend fun updatePage(page: Page): Page

    suspend fun deletePage(page: Page)

    suspend fun deletePages(pages: List<Page>)

    suspend fun deletePages(notebookId: String)

}