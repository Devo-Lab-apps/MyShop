package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PageRepositoryImpl @Inject constructor(
    private val localPageService: LocalPageService,
    private val remotePageService: RemotePageService
) : PageRepository {


    override suspend fun getPages(notebookId: String): Flow<DataState<List<Page>>> = flow {
        emit(DataState.loading<List<Page>>(true))
        try {
            var localPages = localPageService.getPages(notebookId)
            if (localPages.isNullOrEmpty()) {
                val remotePages = remotePageService.getPages(notebookId)
                if (remotePages.isNullOrEmpty()) {
                    throw java.lang.Exception("No pages for the selected notebook")
                }

                localPages = localPageService.insertPages(remotePages)
            }
            emit(DataState.data(localPages))
        } catch (ex: Exception) {
            emit(
                DataState.message<List<Page>>(
                    ex.message ?: "An unknown error occurred. Please retry later."
                )
            )
        }
    }

    override suspend fun insertPages(pages: List<Page>): DataState<List<Page>> {
        return try {
            val newPages = localPageService.insertPages(pages)
            remotePageService.insertPages(newPages)
            DataState.data(newPages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun insertPage(page: Page): DataState<Page> {
        return try {
            val newPage = localPageService.insertPage(page)
            remotePageService.insertPage(newPage)
            DataState.data(newPage)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updatePages(pages: List<Page>): DataState<List<Page>> {
        return try {
            val newPages = remotePageService.updatePages(pages)
            localPageService.updatePages(newPages)
            DataState.data(newPages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updatePage(page: Page): DataState<Page> {
        return try {
            val newPage = remotePageService.updatePage(page)
            localPageService.updatePage(newPage)
            DataState.data(newPage)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deletePage(page: Page): DataState<Page> {
        return try {
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
            remotePageService.deletePages(pages)
            localPageService.deletePages(pages)
            DataState.data(pages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }
}
