package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemotePageMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityNotebook
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityPage
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.util.exceptions.PageNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemotePageServiceFirebaseImpl @Inject constructor(
    private val remoteNotebookMapper: RemoteNotebookMapper,
    private val remotePageMapper: RemotePageMapper
) : RemotePageService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getPages(notebookId: String): List<Page>? {
        val notebookEntity = getEntityNotebook(notebookId) ?: return null
        val pageIds = getNotebookPageIds(notebookEntity)
        return getPages(pageIds)
    }


    override suspend fun insertPages(pages: List<Page>): List<Page> {
        //TODO think of limit on pages
//        val notebookId = pages.first { page -> true }.creatorNotebookId
//        val existingPages = getPages(notebookId).asLiveData().value
//
//        if (existingPages?.size == 3) {
//            return DataState.message("You can't have more than 3 pages per account.")
//        }
        val insertedPages = mutableListOf<Page>()
        FirebaseHelper.runWriteBatch {
            pages.forEach { page ->
                insertedPages.add(insertInDb(page))
            }
        }
        return insertedPages
    }

    override suspend fun insertPage(page: Page): Page {
//        val existingPages = getPages(page.creatorNotebookId).first()
//
//        if (existingPages.size >= 3) {
//            return DataState.message("You can't have more than 3 pages per account.")
//        }
        var updatedPage: Page = page.copy()
        FirebaseHelper.runWriteBatch {
            updatedPage = insertInDb(page)
        }
        return updatedPage
    }

    override suspend fun updatePages(pages: List<Page>): List<Page> {
        val updatedPages = mutableListOf<Page>()

        FirebaseHelper.runUpdateBatch {
            pages.forEach { page ->
                val existing =
                    FirebaseHelper.getPageReference(page.pageId)
                        .get().result
                if (existing?.exists() == false) {
                    throw PageNotFoundException("The page is not present and maybe deleted by another user.")
                }
            }
            pages.forEach { page ->
                updatedPages.add(updateInDb(page))
            }
        }
        return updatedPages
    }

    override suspend fun updatePage(page: Page): Page {
        var updatedPage = page.copy()
        FirebaseHelper.runUpdateBatch {
            val existing =
                FirebaseHelper.getPageReference(page.pageId).get().result
            if (existing?.exists() == false) {
                throw PageNotFoundException("The page is not present and maybe deleted by another user.")
            }
            updatedPage = updateInDb(page)
        }
        return updatedPage
    }

    override suspend fun deletePage(page: Page) {
        val pageId = page.pageId
        FirebaseHelper.runWriteBatch {
            val existing =
                FirebaseHelper.getPageReference(pageId).get().result
            if (existing?.exists() == false) {
                throw PageNotFoundException("The page is not present and maybe deleted by another user.")
            }
            deleteFromDb(pageId)
        }
    }

    override suspend fun deletePages(pages: List<Page>) {
        val pageIds = pages.map { it.pageId }
        FirebaseHelper.runWriteBatch {
            pageIds.forEach { pageId ->
                val existing =
                    FirebaseHelper.getPageReference(pageId).get().result
                if (existing?.exists() == false) {
                    throw PageNotFoundException("The page is already deleted by another user.")
                }
            }
            pageIds.forEach { pageId -> deleteFromDb(pageId) }
        }
    }

    private fun insertInDb(page: Page): Page {
        val pageId = FirebaseHelper.getPageCollection().id
        val data = remotePageMapper.mapToEntity(
            page.copy(
                pageId = pageId
            )
        )
        FirebaseHelper.getPageReference(pageId)
            .set(data)
        return remotePageMapper.mapFromEntity(data)
    }


    private fun updateInDb(page: Page): Page {
        if (page.pageId.isBlank()) {
            throw PageNotFoundException("Invalid page id passed.")
        }
        val data = remotePageMapper.mapToEntity(
            page.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        FirebaseHelper.getPageReference(data.pageId)
            .set(data)
        return remotePageMapper.mapFromEntity(data)
    }

    private fun deleteFromDb(pageId: String) {
        if (pageId.isBlank()) {
            throw PageNotFoundException("Invalid page id passed.")
        }
        FirebaseHelper.getPageReference(pageId)
            .delete()
    }

    private suspend fun getEntityNotebook(notebookId: String): RemoteEntityNotebook? {
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")

        val documentSnapshot = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .get().await()

        return documentSnapshot.toObject(RemoteEntityNotebook::class.java)
    }


    private fun getNotebookPageIds(notebookRemoteEntity: RemoteEntityNotebook): List<String> {
        val notebook = remoteNotebookMapper.mapFromEntity(notebookRemoteEntity)
        return notebook.pages
    }

    /**
     * This method will get the pages for a notebook in a batch of 10 pages.
     */
    private suspend fun getPages(pageIds: List<String>): MutableList<Page> {
        val pageIterations = pageIds.size / 10
        val pages = mutableListOf<Page>()
        FirebaseHelper.runUpdateBatch {
            for (i in 0..pageIterations) {
                val startIndex = i * 10
                val endIndex = ((i + 1) * 10).coerceAtMost(pageIds.size)
                val ids = pageIds.subList(startIndex, endIndex)

                val snapshot = FirebaseHelper.getPageCollection()
                    .whereIn("pageId", ids).get().result

                snapshot?.documents?.let {
                    pages.addAll(
                        it.map { s ->
                            val page = s.toObject(RemoteEntityPage::class.java)!!
                            remotePageMapper.mapFromEntity(page)
                        }
                    )
                }
            }
        }
        return pages
    }
}