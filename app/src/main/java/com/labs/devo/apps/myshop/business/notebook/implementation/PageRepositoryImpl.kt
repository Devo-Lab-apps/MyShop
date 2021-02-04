package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PageRepositoryImpl  @Inject constructor(
    private val pageService: RemotePageService
) : PageRepository {


    override suspend fun getPages(notebookId: String): Flow<List<Page>?> = pageService.getPages(notebookId)

    override suspend fun insertPages(pages: List<Page>): DataState<List<Page>> =
        pageService.insertPages(pages)

    override suspend fun insertPage(page: Page): DataState<Page> =
        pageService.insertPage(page)

    override suspend fun updatePages(pages: List<Page>): DataState<List<Page>> =
        pageService.updatePages(pages)

    override suspend fun updatePage(page: Page): DataState<Page> =
        pageService.updatePage(page)

    override suspend fun deletePage(pageId: String): DataState<String> =
        pageService.deletePage(pageId)

    override suspend fun deletePages(pageIds: List<String>): DataState<String> =
        pageService.deletePages(pageIds)
}