package com.labs.devo.apps.myshop.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

const val recurringEntryLoadKey = "recurring:"

@ExperimentalPagingApi
class RecurringEntryRemoteMediator(
    private val pageId: String?,
    private val forceRefresh: Boolean,
    private val database: NotebookDatabase,
    private val networkService: RemoteRecurringEntryService
) : RemoteMediator<Int, RecurringEntry>() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val recurringEntryDao = database.recurringEntryDao()

    private val remoteKeyDao = database.remoteKeyDao()


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RecurringEntry>
    ): MediatorResult {
        try {
            val remoteKey = "$recurringEntryLoadKey$pageId"
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


            val remoteRecurringEntries = networkService.getRecurringEntries(pageId, loadKey ?: "")

            val endReached =
                if (remoteRecurringEntries.size >= 10) remoteRecurringEntries[9].recurringEntryId
                else null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(remoteKey)
                    if (pageId != null)
                        recurringEntryDao.deleteRecurringEntries(pageId)
                    else
                        recurringEntryDao.deleteAll()
                }

                remoteKeyDao.insertOrReplace(RemoteKey(remoteKey, endReached))
                recurringEntryDao.insertRecurringEntries(remoteRecurringEntries)
            }
            if (pageId == null && endReached == null) {
                preferencesManager.updateRecurringEntriesSynced()
            }
            return MediatorResult.Success(endOfPaginationReached = endReached == null)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val remoteKey = "$recurringEntryLoadKey$pageId"
        val lastModifiedEntry =
            AsyncHelper.runAsync { recurringEntryDao.getLastFetchedEntry(pageId, remoteKey) }
        lastModifiedEntry?.let {
            if (System.currentTimeMillis() - it.modifiedAt > AppConstants.ONE_DAY_MILLIS) {
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