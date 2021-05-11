package com.labs.devo.apps.myshop.data.repo.notebook.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface PageRepository {

    suspend fun getPages(
        notebookId: String,
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Page>>

    suspend fun getPage(pageId: String): Page

    suspend fun insertPage(page: Page): DataState<Page>

    suspend fun updatePage(page: Page): DataState<Page>

    suspend fun deletePage(page: Page): DataState<Page>

    suspend fun deletePages()

    suspend fun syncPages(notebookId: String): DataState<List<Page>>

}