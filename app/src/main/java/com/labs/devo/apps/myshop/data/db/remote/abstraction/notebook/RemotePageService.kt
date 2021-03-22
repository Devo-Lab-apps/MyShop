package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Page

interface RemotePageService {

    suspend fun getPages(notebookId: String, searchQuery: String, startAfter: String?): List<Page>?

    suspend fun insertPages(pages: List<Page>): List<Page>

    suspend fun insertPage(page: Page): Page

    suspend fun updatePages(pages: List<Page>): List<Page>

    suspend fun updatePage(page: Page): Page

    suspend fun deletePage(page: Page)

    suspend fun deletePages(pages: List<Page>)
}