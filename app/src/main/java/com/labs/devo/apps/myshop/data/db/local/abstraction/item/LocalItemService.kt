package com.labs.devo.apps.myshop.data.db.local.abstraction.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.models.item.Item

interface LocalItemService {

    fun getItems(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Item>

    suspend fun createItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun deleteItem(itemId: String)

    suspend fun deleteItems()

}