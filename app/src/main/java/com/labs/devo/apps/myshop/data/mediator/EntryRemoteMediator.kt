package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.AppConstants.ONE_DAY_MILLIS
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.AsyncHelper

//TODO change this for page also
const val entryLoadKey = "page:"

@ExperimentalPagingApi
class EntryRemoteMediator(
    private val pageId: String,
    private val searchQuery: String,
    private val forceRefresh: Boolean,
    private val database: NotebookDatabase,
    private val networkService: RemoteEntryService
) : RemoteMediator<Int, Entry>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val entryDao = database.entryDao()

    private val remoteKeyDao = database.remoteKeyDao()


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Entry>
    ): MediatorResult {
        try {
            var remoteKey = "$entryLoadKey$pageId"
            if (searchQuery.isNotBlank()) {
                remoteKey += ":$searchQuery"
            }
            val loadKey: String? = when (loadType) {
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


            val remoteEntries = networkService.getEntries(pageId, searchQuery, loadKey)

            val endReached =
                if (remoteEntries.size >= 10) remoteEntries[9].entryId
                else null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(remoteKey)
                    entryDao.deleteEntries(pageId)
                }

                remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, endReached))
                entryDao.insertEntries(remoteEntries)
            }
            //TODO put appropriate condition
            return MediatorResult.Success(endOfPaginationReached = endReached == null)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        var remoteKey = "$entryLoadKey$pageId"
        if (searchQuery.isNotBlank()) {
            remoteKey += ":$searchQuery"
        }
        val lastModifiedEntry =
            AsyncHelper.runAsync { entryDao.getLastFetchedEntry(pageId, remoteKey, false) }
        lastModifiedEntry?.let {
            if (System.currentTimeMillis() - it.modifiedAt > ONE_DAY_MILLIS) {
                return InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
        return if (forceRefresh) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}