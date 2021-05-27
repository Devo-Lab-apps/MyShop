package com.labs.devo.apps.myshop.data.repo.item.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.labs.devo.apps.myshop.business.helper.PermissionsHelper
import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemDetailService
import com.labs.devo.apps.myshop.data.db.local.database.database.AppDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemDetailService
import com.labs.devo.apps.myshop.data.mediator.ItemDetailRemoteMediator
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.util.exceptions.ExceptionCatcher.handleExceptionAndReturnErrorMessage
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ItemDetailRepositoryImpl @Inject constructor(
    val notebookDatabase: NotebookDatabase,
    val appDatabase: AppDatabase,
    private val itemDatabase: ItemDatabase,
    private val localItemDetailService: LocalItemDetailService,
    private val remoteItemDetailService: RemoteItemDetailService
) : ItemDetailRepository {

    private val itemDetailsPageSize = 12
    private val itemDetailsMaxSize = 40

    override suspend fun getItemDetails(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<ItemDetail>> {
        return try {
            Pager(
                config = PagingConfig(pageSize = itemDetailsPageSize, maxSize = itemDetailsMaxSize),
                remoteMediator = ItemDetailRemoteMediator(
                    searchQuery,
                    forceRefresh,
                    itemDatabase,
                    notebookDatabase,
                    appDatabase,
                    remoteItemDetailService
                ),
                pagingSourceFactory = {
                    localItemDetailService.getItemDetails(searchQuery, orderBy)
                }
            ).flow
        } catch (ex: Exception) {
            flow { PagingData.empty<ItemDetail>() }
        }
    }

    override suspend fun getItemDetail(itemId: String): DataState<ItemDetail> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.GET_ITEM)
            var itemDetail = localItemDetailService.getItemDetail(itemId)
            if (itemDetail == null) {
                itemDetail = remoteItemDetailService.getItemDetail(itemId)
            }
            DataState.data(itemDetail)
        } catch (ex: Exception) {
            DataState.message(
                handleExceptionAndReturnErrorMessage(ex)
            )
        }
    }

    override suspend fun createItemDetail(itemDetail: ItemDetail): DataState<ItemDetail> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_ITEM)
            val insertedEntry = remoteItemDetailService.createItemDetail(itemDetail)
            localItemDetailService.createItemDetail(insertedEntry)
            DataState.data(insertedEntry)
        } catch (ex: Exception) {
            DataState.message(
                handleExceptionAndReturnErrorMessage(ex)
            )
        }
    }

    override suspend fun updateItemDetail(itemDetail: ItemDetail): DataState<ItemDetail> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.CREATE_ITEM)
            val updatedEntry = remoteItemDetailService.updateItemDetail(itemDetail)
            localItemDetailService.updateItemDetail(updatedEntry)
            DataState.data(updatedEntry)
        } catch (ex: Exception) {
            DataState.message(
                handleExceptionAndReturnErrorMessage(ex)
            )
        }
    }

    override suspend fun deleteItemDetail(itemDetail: ItemDetail): DataState<Void> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_ITEM)
            remoteItemDetailService.deleteItemDetail(itemDetail.itemDetailId)
            localItemDetailService.deleteItemDetail(itemDetail.itemDetailId)
            DataState.data()
        } catch (ex: Exception) {
            DataState.message(
                handleExceptionAndReturnErrorMessage(ex)
            )
        }
    }

    override suspend fun deleteAll(): DataState<Void> {
        return try {
            PermissionsHelper.checkPermissions(Permissions.DELETE_ITEM)
            localItemDetailService.deleteItemDetails()
            DataState.data()
        } catch (ex: Exception) {
            DataState.message(
                handleExceptionAndReturnErrorMessage(ex)
            )
        }
    }

    override suspend fun syncItemDetails() {
        TODO("Not yet implemented")
    }
}