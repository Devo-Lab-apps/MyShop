package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Page

interface LocalPageService {


    suspend fun getPages(notebookId: String): List<Page>?

    suspend fun insertPages(pages: List<Page>): List<Page>

    suspend fun insertPage(page: Page): Page

    suspend fun updatePages(pages: List<Page>): List<Page>

    suspend fun updatePage(page: Page): Page

    suspend fun deletePage(page: Page)

    suspend fun deletePages(pages: List<Page>)

}