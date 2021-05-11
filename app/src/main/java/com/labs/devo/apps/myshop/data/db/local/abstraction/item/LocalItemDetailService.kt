package com.labs.devo.apps.myshop.data.db.local.abstraction.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.item.ItemDetail

interface LocalItemDetailService {

    fun getLocalItemDetails(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, ItemDetail>

    suspend fun createLocalItemDetail(itemDetail: ItemDetail)

    suspend fun updateItemDetail(itemDetail: ItemDetail)

    suspend fun deleteItemDetail(itemId: String)

    suspend fun deleteItemDetails()
}