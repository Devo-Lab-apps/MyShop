package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.Transaction
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

        FirebaseHelper.runTransaction { transaction ->
            pages.forEach { page ->
                val ref =
                    FirebaseHelper.getPageReference(page.pageId)
                val existing = transaction.get(ref)
                if (!existing.exists()) {
                    throw PageNotFoundException("The page is not present and maybe deleted by another user.")
                }
            }
            pages.forEach { page ->
                updatedPages.add(updateInDb(page, transaction))
            }
        }
        return updatedPages
    }

    override suspend fun updatePage(page: Page): Page {
        var updatedPage = page.copy()
        FirebaseHelper.runTransaction { transaction ->
            val ref =
                FirebaseHelper.getPageReference(page.pageId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw PageNotFoundException("The page is not present and maybe deleted by another user.")
            }
            updatedPage = updateInDb(page, transaction)
        }
        return updatedPage
    }

    override suspend fun deletePage(page: Page) {
        val pageId = page.pageId
        FirebaseHelper.runTransaction { transaction ->
            val ref =
                FirebaseHelper.getPageReference(pageId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw PageNotFoundException("The page is not present and maybe deleted by another user.")
            }
            deleteFromDb(pageId, transaction)
        }
    }

    override suspend fun deletePages(pages: List<Page>) {
        val pageIds = pages.map { it.pageId }
        FirebaseHelper.runTransaction { transaction ->
            pageIds.forEach { pageId ->
                val ref =
                    FirebaseHelper.getPageReference(pageId)
                val existing = transaction.get(ref)
                if (!existing.exists()) {
                    throw PageNotFoundException("The page is already deleted by another user.")
                }
            }
            pageIds.forEach { pageId -> deleteFromDb(pageId, transaction) }
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


    private fun updateInDb(page: Page, transaction: Transaction): Page {
        if (page.pageId.isBlank()) {
            throw PageNotFoundException("Invalid page id passed.")
        }
        val data = remotePageMapper.mapToEntity(
            page.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
        transaction.set(
            FirebaseHelper.getPageReference(data.pageId), data
        )
        return remotePageMapper.mapFromEntity(data)
    }

    private fun deleteFromDb(pageId: String, transaction: Transaction) {
        if (pageId.isBlank()) {
            throw PageNotFoundException("Invalid page id passed.")
        }
        transaction.delete(FirebaseHelper.getPageReference(pageId))
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
        FirebaseHelper.runTransaction {
            for (i in 0..pageIterations) {
                val startIndex = i * 10
                val endIndex = ((i + 1) * 10).coerceAtMost(pageIds.size)
                val ids = pageIds.subList(startIndex, endIndex)

                FirebaseHelper.getPageCollection()
                    .whereIn("pageId", ids).get().addOnSuccessListener { snapshot ->
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
        }
        return pages
    }
}