package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.AppDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemDetailService
import com.labs.devo.apps.myshop.data.db.remote.implementation.item.itemDetailGetLimit
import com.labs.devo.apps.myshop.data.db.remote.implementation.item.itemGetLimit
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.view.util.AsyncHelper

const val itemDetailsLoadKey = "item_detail"

class ItemDetailRemoteMediator(
    private val searchQuery: String,
    private val forceRefresh: Boolean,
    private val itemDatabase: ItemDatabase,
    notebookDatabase: NotebookDatabase,
    appDatabase: AppDatabase,
    private val networkService: RemoteItemDetailService
) : RemoteMediator<Int, ItemDetail>() {

    private val itemDetailDao = itemDatabase.itemDetailDao()

    private val remoteKeyDao = appDatabase.remoteKeyDao()

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemDetail>
    ): MediatorResult {
        try {
            var remoteKey = itemDetailsLoadKey
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

            val remoteItems = networkService.getItemDetails(searchQuery, loadKey)

            val endReached = if (remoteItems.size == itemDetailGetLimit) remoteItems[9].modifiedAt
            else null


            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.deleteByQuery(itemDetailsLoadKey)
                itemDetailDao.deleteItemDetails()
            }

            remoteKeyDao.createOrReplace(RemoteKey(remoteKey, endReached.toString()))
            itemDetailDao.createItemDetails(remoteItems)

            //TODO put appropriate condition
            return MediatorResult.Success(endOfPaginationReached = endReached == null)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        if (forceRefresh) return InitializeAction.LAUNCH_INITIAL_REFRESH
        var remoteKey = itemDetailsLoadKey
//        if (searchQuery.isNotBlank()) {
//            remoteKey += ":$searchQuery"
//        }
        val lastModifiedItem = AsyncHelper.runAsync {
            itemDetailDao.getLastFetchedItemDetail()
        }
        lastModifiedItem?.let {
            if (System.currentTimeMillis() - it.modifiedAt > AppConstants.ONE_DAY_MILLIS) {
                AsyncHelper.runAsync {
                    remoteKeyDao.createOrReplace(RemoteKey(remoteKey, it.modifiedAt.toString()))
                }
            }
        }
        return InitializeAction.SKIP_INITIAL_REFRESH
    }
}