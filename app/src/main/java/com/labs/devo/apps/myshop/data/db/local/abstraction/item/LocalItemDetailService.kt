package com.labs.devo.apps.myshop.data.db.local.abstraction.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.item.ItemDetail

interface LocalItemDetailService {

    fun getItemDetails(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, ItemDetail>

    /**
     * Get Item with the itemId and not the itemDetailId.
     */
    suspend fun getItemDetail(itemId: String): ItemDetail?

    suspend fun createItemDetail(itemDetail: ItemDetail)

    suspend fun updateItemDetail(itemDetail: ItemDetail)

    suspend fun deleteItemDetail(itemId: String)

    suspend fun deleteItemDetails()
}