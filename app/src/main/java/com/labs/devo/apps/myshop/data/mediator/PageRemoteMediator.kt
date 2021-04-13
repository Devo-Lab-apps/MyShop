package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.AsyncHelper

const val pagesLoadKey = "notebook:"

@ExperimentalPagingApi
class PageRemoteMediator(
    private val notebookId: String,
    private val searchQuery: String,
    private val forceRefresh: Boolean,
    private val database: NotebookDatabase,
    private val networkService: RemotePageService
) : RemoteMediator<Int, Page>() {

    private val pageDao = database.pageDao()

    private val remoteKeyDao = database.remoteKeyDao()

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Page>
    ): MediatorResult {
        try {
            var remoteKey = "$pagesLoadKey$notebookId"
            if (searchQuery.isNotBlank()) {
                remoteKey += ":$searchQuery"
            }
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val rk = database.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(remoteKey)
                    }
                    if (rk != null) {
                        if (rk.nextKey == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                        rk.nextKey
                    } else {
                        null
                    }
                }
            }


            val remotePages = networkService.getPages(notebookId, searchQuery, loadKey)

            val endReached = if (remotePages == null) {
                null
            } else {
                if (remotePages.size >= 10) remotePages[9].pageId
                else null
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery("$pagesLoadKey$notebookId")
                    pageDao.deletePages(notebookId)
                }

                remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, endReached))
                pageDao.insertPages(remotePages ?: listOf())
            }
            //TODO put appropriate condition
            return MediatorResult.Success(endOfPaginationReached = endReached == null)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        if (searchQuery.isNotBlank()) {
            val remoteKey = "$pagesLoadKey$notebookId:$searchQuery"
            val lastModifiedPage =
                AsyncHelper.runAsync { pageDao.getLastModifiedDate(notebookId, remoteKey) }
            lastModifiedPage?.let {
                if (System.currentTimeMillis() - it.modifiedAt > 86400 * 1000) {
                    return InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }
        }
        return if (forceRefresh) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}