package com.labs.devo.apps.myshop.data.db.local.abstraction.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.item.Item

interface LocalItemService {

    fun getLocalItems(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Item>

    suspend fun createLocalItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun deleteItem(itemId: String)

    suspend fun deleteItems()

}