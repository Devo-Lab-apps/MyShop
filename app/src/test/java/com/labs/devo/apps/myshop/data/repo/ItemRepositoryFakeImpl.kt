package com.labs.devo.apps.myshop.data.repo

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ItemRepositoryFakeImpl: ItemRepository {

    private val items = mutableListOf<Item>()

    override suspend fun getItems(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<Item>> {
        return flow {
            emit(PagingData.from(items))
        }
    }

    override suspend fun createItem(item: Item): DataState<Item> {
        items.add(item)
        return DataState.data(item)
    }

    override suspend fun updateItem(item: Item): DataState<Item> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(item: Item): DataState<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll(): DataState<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun syncItems() {
        TODO("Not yet implemented")
    }
}