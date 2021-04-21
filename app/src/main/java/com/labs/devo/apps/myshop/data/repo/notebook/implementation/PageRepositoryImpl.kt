package com.labs.devo.apps.myshop.data.repo.notebook.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper.checkPermissions
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.mediator.PageRemoteMediator
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.util.exceptions.PageNotFoundException
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PageRepositoryImpl @Inject constructor(
    private val localPageService: LocalPageService,
    private val notebookDatabase: NotebookDatabase,
    private val remotePageService: RemotePageService
) : PageRepository {


    override suspend fun getPages(
        notebookId: String,
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Page>> = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100),
        remoteMediator = PageRemoteMediator(
            notebookId,
            searchQuery,
            forceRefresh,
            notebookDatabase,
            remotePageService
        ),
        pagingSourceFactory = { localPageService.getPages(notebookId, searchQuery, orderBy) }
    ).flow

    override suspend fun getPage(pageId: String): Page {
        try {
            checkPermissions(Permissions.GET_NOTEBOOK)
            val localNotebook = localPageService.getPage(pageId)
            return localNotebook ?: throw PageNotFoundException()
        } catch (ex: java.lang.Exception) {
            //TOdO clog
            throw PageNotFoundException()
        }
    }

    override suspend fun insertPages(pages: List<Page>): DataState<List<Page>> {
        return try {
            checkPermissions(Permissions.CREATE_PAGE)
            val newPages = remotePageService.insertPages(pages)
            localPageService.insertPages(newPages)
            DataState.data(newPages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertPage(page: Page): DataState<Page> {
        return try {
            checkPermissions(Permissions.CREATE_PAGE)
            val newPage = remotePageService.insertPage(page)
            localPageService.insertPage(newPage)
            DataState.data(newPage)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updatePages(pages: List<Page>): DataState<List<Page>> {
        return try {
            checkPermissions(Permissions.CREATE_PAGE)
            val updatedPages = remotePageService.updatePages(pages)
            localPageService.updatePages(updatedPages)
            DataState.data(updatedPages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updatePage(page: Page): DataState<Page> {
        return try {
            checkPermissions(Permissions.CREATE_PAGE)
            val updatedPage = remotePageService.updatePage(page)
            localPageService.updatePage(updatedPage)
            DataState.data(updatedPage)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deletePage(page: Page): DataState<Page> {
        return try {
            checkPermissions(Permissions.DELETE_PAGE)
            remotePageService.deletePage(page)
            localPageService.deletePage(page)
            DataState.data(page)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deletePages(pages: List<Page>): DataState<List<Page>> {
        return try {
            checkPermissions(Permissions.DELETE_PAGE)
            remotePageService.deletePages(pages)
            localPageService.deletePages(pages)
            DataState.data(pages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deletePages() {
        localPageService.deletePages()
    }

    override suspend fun syncPages(notebookId: String): DataState<List<Page>> {
        return try {
            localPageService.deletePages(notebookId)
            //TODO add SearchQuery and startAfter
            val remotePages = remotePageService.getPages(notebookId, "", "")
            if (remotePages.isNullOrEmpty()) {
                throw java.lang.Exception("No pages for the selected notebook")
            }
            localPageService.insertPages(remotePages)
            DataState.data(remotePages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}
