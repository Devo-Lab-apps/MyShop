package com.labs.devo.apps.myshop.business.notebook.abstraction

import androidx.room.RawQuery
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.QueryParams
import kotlinx.coroutines.flow.Flow

interface PageRepository {

    suspend fun getPages(notebookId: String, queryParams: QueryParams): Flow<DataState<List<Page>>>

    suspend fun insertPages(pages: List<Page>): DataState<List<Page>>

    suspend fun insertPage(page: Page): DataState<Page>

    suspend fun updatePages(pages: List<Page>): DataState<List<Page>>

    suspend fun updatePage(page: Page): DataState<Page>

    suspend fun deletePage(page: Page): DataState<Page>

    suspend fun deletePages(pages: List<Page>): DataState<List<Page>>

    suspend fun syncPages(notebookId: String): DataState<List<Page>>

}