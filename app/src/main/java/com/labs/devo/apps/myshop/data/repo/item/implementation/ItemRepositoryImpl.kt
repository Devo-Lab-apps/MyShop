package com.labs.devo.apps.myshop.data.repo.item.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemService
import com.labs.devo.apps.myshop.data.db.local.database.database.AppDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemService
import com.labs.devo.apps.myshop.data.mediator.ItemRemoteMediator
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepositoryImpl
@Inject constructor(
    val notebookDatabase: NotebookDatabase,
    val appDatabase: AppDatabase,
    private val itemDatabase: ItemDatabase,
    private val localItemService: LocalItemService,
    private val remoteItemService: RemoteItemService
) : ItemRepository {

    private val itemsPageSize = 12
    private val itemsMaxSize = 40

    override suspend fun getItems(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Item>> = Pager(
        config = PagingConfig(pageSize = itemsPageSize, maxSize = itemsMaxSize),
        remoteMediator = ItemRemoteMediator(
            searchQuery,
            forceRefresh,
            itemDatabase,
            notebookDatabase,
            appDatabase,
            remoteItemService
        ),
        pagingSourceFactory = {
            localItemService.getItems(searchQuery, orderBy)
        }
    ).flow

    override suspend fun createItem(item: Item): DataState<Item> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_ITEM)
            val insertedEntry = remoteItemService.createItem(item)
            localItemService.createItem(insertedEntry)
            DataState.data(insertedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun updateItem(item: Item): DataState<Item> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_ITEM)
            val updatedEntry = remoteItemService.updateItem(item)
            localItemService.updateItem(updatedEntry)
            DataState.data(updatedEntry)
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteItem(item: Item): DataState<Void> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_ITEM)
            remoteItemService.deleteItem(item.itemId)
            localItemService.deleteItem(item.itemId)
            DataState.data()
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun deleteAll(): DataState<Void> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_ITEM)
            localItemService.deleteItems()
            DataState.data()
        } catch (ex: Exception) {
            DataState.message(
                ex.message ?: "An unknown error occurred. Please retry later."
            )
        }
    }

    override suspend fun syncItems() {
        TODO("Not yet implemented")
    }
}