package com.labs.devo.apps.myshop.data.db.remote.abstraction.item

import com.labs.devo.apps.myshop.data.models.item.Item

interface RemoteItemService {

    suspend fun getItems(searchQuery: String, startAfter: String?): List<Item>

    suspend fun createItem(item: Item): Item

    suspend fun updateItem(item: Item): Item

    suspend fun deleteItem(itemId: String)

}