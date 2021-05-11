package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemotePageMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityNotebook
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityPage
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.util.exceptions.*
import com.labs.devo.apps.myshop.util.extensions.isValidEmail
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemotePageServiceFirebaseImpl @Inject constructor(
    private val remoteNotebookMapper: RemoteNotebookMapper,
    private val remotePageMapper: RemotePageMapper,
    private val localNotebookService: LocalNotebookService
) : RemotePageService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getPages(
        notebookId: String,
        searchQuery: String,
        startAfter: String?
    ): List<Page>? {
        val notebookEntity = getEntityNotebook(notebookId) ?: return null
        val pageIds = getNotebookPageIds(notebookEntity)
        return get(pageIds, searchQuery, startAfter)
    }

    override suspend fun insertPage(page: Page): Page {
        if (!page.consumerUserId.isValidEmail()) {
            throw java.lang.Exception("Invalid receiver's email.")
        }
        var createdPage: Page = page.copy()
        FirebaseHelper.runTransaction { transaction ->
            val notebookEntity =
                getEntityNotebookFromTransaction(page.creatorNotebookId, transaction)
            if (notebookEntity.pages.size >= 50) {
                throw PageLimitExceededException()
            }
            createdPage = insertInDb(page, transaction)
            addPagesInNotebookEntity(notebookEntity, listOf(createdPage.pageId), transaction)
        }
        return createdPage
    }

    override suspend fun updatePage(page: Page): Page {
        var updatedPage = page.copy()
        FirebaseHelper.runTransaction { transaction ->
            val ref =
                FirebaseHelper.getPageReference(page.pageId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw PageNotFoundException()
            }
            updatedPage = updateInDb(page, transaction)
        }
        return updatedPage
    }

    override suspend fun deletePage(page: Page) {
        val pageId = page.pageId
        //TODO decide whether to delete entries also
        FirebaseHelper.runTransaction { transaction ->
            val ref =
                FirebaseHelper.getPageReference(pageId)
            val existing = transaction.get(ref)
            if (!existing.exists()) {
                throw PageNotFoundException()
            }
            deleteFromDb(pageId, transaction)
        }
    }

    private fun insertInDb(page: Page, transaction: Transaction): Page {
        val pageId = FirebaseHelper.getPageReference().id
        val foreignSnapshot = foreignSnapshot(page, transaction, pageId)
        if (foreignSnapshot.exists()) {
            insertInForeign(foreignSnapshot, transaction, pageId)
        }
        val data = remotePageMapper.mapToEntity(
            page.copy(
                pageId = pageId
            )
        )
        val pageRef = FirebaseHelper.getPageReference(pageId)
        transaction.set(pageRef, data)
        return remotePageMapper.mapFromEntity(data)
    }

    private fun insertInForeign(
        foreignSnapshot: DocumentSnapshot,
        transaction: Transaction,
        pageId: String
    ) {
        val user = foreignSnapshot.toObject(User::class.java)!!
        val foreignRef = FirebaseHelper.getNotebookReference(
            user.accountId,
            FirebaseConstants.foreignNotebookKey
        )
        transaction.update(
            foreignRef, mapOf(
                "pages" to FieldValue.arrayUnion(pageId)
            )
        )
    }

    private fun foreignSnapshot(
        page: Page,
        transaction: Transaction,
        pageId: String
    ): DocumentSnapshot {
        val receiver = page.consumerUserId
        val ref = FirebaseHelper.getUsersDocReference(receiver)
        return transaction.get(ref)
    }


    private fun updateInDb(page: Page, transaction: Transaction): Page {
        if (page.pageId.isBlank()) {
            throw PageNotFoundException()
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
        //TODO remove from notebook list
        if (pageId.isBlank()) {
            throw PageNotFoundException()
        }
        transaction.delete(FirebaseHelper.getPageReference(pageId))
    }

    /**
     * Get notebook entity from firebase with notebook id.
     */
    private suspend fun getEntityNotebook(notebookId: String): RemoteEntityNotebook? {
        val user = UserManager.user ?: throw UserNotInitializedException()

        val documentSnapshot = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
            .get().await()

        return documentSnapshot.toObject(RemoteEntityNotebook::class.java)
    }


    /**
     * Get pages from notebook entity.
     */
    private fun getNotebookPageIds(notebookRemoteEntity: RemoteEntityNotebook): List<String> {
        val notebook = remoteNotebookMapper.mapFromEntity(notebookRemoteEntity)
        return notebook.pages
    }

    /**
     * This method will get the pages for a notebook in a batch of 10 pages/request.
     */
    //TODO paginate
    private suspend fun get(
        pids: List<String>,
        searchQuery: String,
        startAfter: String?
    ): MutableList<Page> {
        val pageIds = if (startAfter == null) {
            pids.subList(0, 10.coerceAtMost(pids.size))
        } else {
            val sortedList =
                pids.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))
            val startAfterIndex = sortedList.indexOf(startAfter)
            pids.subList(startAfterIndex, (startAfterIndex + 10).coerceAtMost(pids.size))
        }

        if (pageIds.isEmpty()) {
            throw NoPagesForNotebookException()
        }
        val pages = mutableListOf<Page>()
        val snapshot = FirebaseHelper.getPageCollection()
            .whereIn("pageId", pageIds).get().await()
        snapshot?.documents?.let {
            pages.addAll(
                it.map { s ->
                    val page = s.toObject(RemoteEntityPage::class.java)!!
                    remotePageMapper.mapFromEntity(page)
                }
            )
        }
        return pages
    }

    /**
     * Add pages in notebook entity in firebase.
     */
    private fun addPagesInNotebookEntity(
        notebookEntity: RemoteEntityNotebook,
        pageIds: List<String>,
        transaction: Transaction
    ) {
        val notebookId = notebookEntity.notebookId
        val user = UserManager.user ?: throw UserNotInitializedException()
        val notebookRef = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
        notebookEntity.pages.addAll(pageIds)
        //run it in background because it is local
        AsyncHelper.runAsyncInBackground {
            localNotebookService.updateNotebook(remoteNotebookMapper.mapFromEntity(notebookEntity))
        }
        transaction.set(notebookRef, notebookEntity)
    }

    /**
     * Get entity notebook from transaction obj for notebookId.
     */
    private fun getEntityNotebookFromTransaction(
        notebookId: String,
        transaction: Transaction
    ): RemoteEntityNotebook {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val notebookRef = FirebaseHelper.getNotebookReference(user.accountId, notebookId)
        val documentSnapshot = transaction.get(notebookRef)
        return documentSnapshot.toObject(RemoteEntityNotebook::class.java)
            ?: throw NotebookNotFoundException()
    }
}