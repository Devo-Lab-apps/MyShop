package com.labs.devo.apps.myshop.business.notebook.abstraction

import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface PageRepository {

    suspend fun getPages(notebookId: String): Flow<List<Page>?>

    suspend fun insertPages(pages: List<Page>): DataState<List<Page>>

    suspend fun insertPage(page: Page): DataState<Page>

    suspend fun updatePages(pages: List<Page>): DataState<List<Page>>

    suspend fun updatePage(page: Page): DataState<Page>

    suspend fun deletePage(pageId: String): DataState<String>

    suspend fun deletePages(pageIds: List<String>): DataState<String>

}