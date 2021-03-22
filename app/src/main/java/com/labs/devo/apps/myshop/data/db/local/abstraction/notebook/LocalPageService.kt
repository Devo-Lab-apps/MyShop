package com.labs.devo.apps.myshop.data.db.local.abstraction.notebook

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.notebook.Page

interface LocalPageService {


    fun getPages(
        notebookId: String,
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Page>

    suspend fun getPage(pageId: String): Page?

    suspend fun insertPages(pages: List<Page>)

    suspend fun insertPage(page: Page)

    suspend fun updatePages(pages: List<Page>)

    suspend fun updatePage(page: Page)

    suspend fun deletePage(page: Page)

    suspend fun deletePages(pages: List<Page>)

    suspend fun deletePages(notebookId: String)

}