package com.labs.devo.apps.myshop.data.repo.item.abstraction

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    suspend fun getItems(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Item>>

    suspend fun createItem(item: Item): DataState<Item>

    suspend fun updateItem(item: Item): DataState<Item>

    suspend fun deleteItem(item: Item): DataState<Void>

    suspend fun deleteAll(): DataState<Void>

    suspend fun syncItems()

}