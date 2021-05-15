package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemService
import com.labs.devo.apps.myshop.data.db.remote.implementation.item.itemGetLimit
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.view.util.AsyncHelper

const val itemsLoadKey = "item"

class ItemRemoteMediator(
    private val searchQuery: String,
    private val forceRefresh: Boolean,
    private val itemDatabase: ItemDatabase,
    notebookDatabase: NotebookDatabase,
    private val networkService: RemoteItemService
) : RemoteMediator<Int, Item>() {

    private val itemDao = itemDatabase.itemDao()

    private val remoteKeyDao = notebookDatabase.remoteKeyDao()

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Item>
    ): MediatorResult {
        try {
            var remoteKey = itemsLoadKey
//            if (searchQuery.isNotBlank()) {
//                remoteKey += ":$searchQuery"
//            }
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val rk = remoteKeyDao.remoteKeyByQuery(remoteKey)
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

            val remoteItems = networkService.getItems(searchQuery, loadKey)

            val endReached = if (remoteItems.size == itemGetLimit) remoteItems[9].modifiedAt
            else null

            itemDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(itemsLoadKey)
                    itemDao.deleteItems()
                }

                remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, endReached.toString()))
                itemDao.insertItems(remoteItems)
            }
            //TODO put appropriate condition
            return MediatorResult.Success(endOfPaginationReached = endReached == null)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        if (forceRefresh) return InitializeAction.LAUNCH_INITIAL_REFRESH
        var remoteKey = itemsLoadKey
//        if (searchQuery.isNotBlank()) {
//            remoteKey += ":$searchQuery"
//        }
        val lastModifiedItem = AsyncHelper.runAsync { itemDao.getLastFetchedItem() }
        lastModifiedItem?.let {
            if (System.currentTimeMillis() - it.modifiedAt > AppConstants.ONE_DAY_MILLIS) {
                AsyncHelper.runAsync {
                    remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, it.modifiedAt.toString()))
                }
            }
        }
        return InitializeAction.SKIP_INITIAL_REFRESH
    }
}