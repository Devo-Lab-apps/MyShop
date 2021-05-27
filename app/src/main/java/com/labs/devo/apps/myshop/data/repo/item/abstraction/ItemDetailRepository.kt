package com.labs.devo.apps.myshop.data.repo.item.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface ItemDetailRepository {

    suspend fun getItemDetails(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<ItemDetail>>

    suspend fun getItemDetail(itemId: String): DataState<ItemDetail>

    suspend fun createItemDetail(itemDetail: ItemDetail): DataState<ItemDetail>

    suspend fun updateItemDetail(itemDetail: ItemDetail): DataState<ItemDetail>

    suspend fun deleteItemDetail(itemDetail: ItemDetail): DataState<Void>

    suspend fun deleteAll(): DataState<Void>

    suspend fun syncItemDetails()

}