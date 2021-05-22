package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Page

interface RemotePageService {

    suspend fun getPages(notebookId: String, searchQuery: String, startAfter: String?): List<Page>?

    suspend fun createPage(page: Page): Page

    suspend fun updatePage(page: Page): Page

    suspend fun deletePage(page: Page)
}