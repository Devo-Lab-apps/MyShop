package com.labs.devo.apps.myshop.data.repo

import androidx.paging.PagingData
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.Flow

class ItemDetailRepositoryFakeImpl:ItemDetailRepository {

    val itemDetails = mutableListOf<ItemDetail>()

    override suspend fun getItemDetails(
        searchQuery: String,
        orderBy: String,
        forceRefresh: Boolean
    ): Flow<PagingData<ItemDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemDetail(itemId: String): DataState<ItemDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun createItemDetail(itemDetail: ItemDetail): DataState<ItemDetail> {
        itemDetails.add(itemDetail)
        return DataState.data(itemDetail)
    }

    override suspend fun updateItemDetail(itemDetail: ItemDetail): DataState<ItemDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItemDetail(itemDetail: ItemDetail): DataState<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll(): DataState<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun syncItemDetails() {
        TODO("Not yet implemented")
    }
}