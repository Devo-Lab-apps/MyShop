package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.NotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.PageMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityNotebook
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityPage
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemotePageServiceFirebaseImpl @Inject constructor(
    private val notebookMapper: NotebookMapper,
    private val pageMapper: PageMapper
) : RemotePageService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getPages(notebookId: String): Flow<List<Page>?> {
        return channelFlow {
            val notebookEntity = getEntityNotebook(notebookId)
            if (notebookEntity == null) {
                offer(null)
                return@channelFlow
            }
            val pageIds = getNotebookPageIds(notebookEntity)
            offer(getPages(pageIds))
        }
    }


    override suspend fun insertPages(pages: List<Page>): DataState<List<Page>> {
        //TODO think of limit on pages
//        val notebookId = pages.first { page -> true }.creatorNotebookId
//        val existingPages = getPages(notebookId).asLiveData().value
//
//        if (existingPages?.size == 3) {
//            return DataState.message("You can't have more than 3 pages per account.")
//        }
        val insertedPages = mutableListOf<Page>()
        return try {
            FirebaseHelper.runWriteBatch {
                pages.forEach { page ->
                    insertedPages.add(insertInDb(page))
                }
            }
            DataState.data(data = insertedPages)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun insertPage(page: Page): DataState<Page> {
//        val existingPages = getPages(page.creatorNotebookId).first()
//
//        if (existingPages.size >= 3) {
//            return DataState.message("You can't have more than 3 pages per account.")
//        }
        var updatedPage: Page? = null
        return try {
            FirebaseHelper.runWriteBatch {
                updatedPage = insertInDb(page)
            }
            DataState.data(data = updatedPage)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun updatePages(pages: List<Page>): DataState<List<Page>> {
        val updatedData = mutableListOf<Page>()
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        pages.forEach { page ->
            val existing =
                FirebaseHelper.getPageReference(page.pageId)
                    .get().await()
            if (!existing?.exists()!!) {
                return DataState.message("The page is not present and is deleted by another user.")
            }
        }
        return try {
            FirebaseHelper.runUpdateBatch {
                pages.forEach { page ->
                    updatedData.add(updateInDb(page))
                }
            }
            DataState.data(updatedData)
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun updatePage(page: Page): DataState<Page> {
        return try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            val existing =
                FirebaseHelper.getPageReference(page.pageId).get()
                    .await()
            if (!existing.exists()) {
                return DataState.message("The page is not present and maybe deleted by another user.")
            }
            DataState.data(updateInDb(page))
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun deletePage(pageId: String): DataState<String> {
        return try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            val existing =
                FirebaseHelper.getPageReference(pageId).get().await()
            if (!existing.exists()) {
                return DataState.data("The page is already deleted by another user.")
            }
            deleteFromDb(pageId)
            DataState.data("Page deleted.")
        } catch (ex: java.lang.Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }
    }

    override suspend fun deletePages(pageIds: List<String>): DataState<String> {
        try {
            val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
            pageIds.forEach { pageId ->
                val existing =
                    FirebaseHelper.getPageReference(pageId).get().await()
                if (!existing.exists()) {
                    return DataState.data("The page is already deleted by another user.")
                }
            }
            FirebaseHelper.runWriteBatch {
                pageIds.forEach { pageId -> deleteFromDb(pageId) }
            }
            return DataState.message("Pages deleted.")
        } catch (ex: java.lang.Exception) {
            return DataState.message(
                ex.message ?: "An unknown error occurred. Please try again later"
            )
        }

    }

    private fun checkIfForeign(page: Page) {
        if (page.pageName == "Foreign" || page.pageId == "foreign") {
            throw java.lang.Exception("You can't perform any operation on foreign page")
        }
    }

    private fun checkIfForeign(pageId: String) {
        if (pageId == "foreign") {
            throw java.lang.Exception("You can't perform any operation on foreign page")
        }
    }


    private fun insertInDb(page: Page): Page {
//        checkIfForeign(page)
//        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        val pageId = FirebaseHelper.getPageCollection().id
        val data = pageMapper.mapToEntity(
            page.copy(
                pageId = pageId
            )
        )
        FirebaseHelper.getPageReference(pageId)
            .set(data)
        return pageMapper.mapFromEntity(data)
    }


    private fun updateInDb(page: Page): Page {
        checkIfForeign(page)
//        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")
        if (page.pageId.isBlank()) {
            throw java.lang.Exception("Invalid page id passed.")
        }
        val data = pageMapper.mapToEntity(
            page.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        FirebaseHelper.getPageReference(data.pageId)
            .set(data)
        return pageMapper.mapFromEntity(data)
    }

    private fun deleteFromDb(pageId: String) {
        checkIfForeign(pageId)
//        val user = UserManager.user ?: throw Exception("User not initialized")
        if (pageId.isBlank()) {
            throw java.lang.Exception("Invalid page id passed.")
        }
        FirebaseHelper.getPageReference(pageId)
            .delete()
    }

    private suspend fun getEntityNotebook(notebookId: String): EntityNotebook? {
        val user = UserManager.user ?: throw UserNotInitializedException("User not initialized")

        val documentSnapshot = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .get().await()

        return documentSnapshot.toObject(EntityNotebook::class.java)
    }


    private fun getNotebookPageIds(notebookEntity: EntityNotebook): List<String> {
        val notebook = notebookMapper.mapFromEntity(notebookEntity)
        return notebook.pages
    }

    private suspend fun getPages(pageIds: List<String>): MutableList<Page> {
        val pageIterations = pageIds.size / 10
        val pages = mutableListOf<Page>()
        for (i in 0..pageIterations) {
            val startIndex = i * 10
            val endIndex = ((i + 1) * 10).coerceAtMost(pageIds.size)
            val ids = pageIds.subList(startIndex, endIndex)

            val snapshot = FirebaseHelper.getPageCollection()
                .whereIn("pageId", ids).get().await()

            pages.addAll(
                snapshot.documents.map { s ->
                    val page = s.toObject(EntityPage::class.java)!!
                    pageMapper.mapFromEntity(page)
                }
            )
        }
        return pages
    }


}