package com.labs.devo.apps.myshop.data.db.remote.abstraction.item

import com.labs.devo.apps.myshop.data.models.item.ItemDetail

interface RemoteItemDetailService {

    suspend fun getItemDetails(searchQuery: String, startAfter: String?): List<ItemDetail>

    suspend fun createItemDetail(itemDetail: ItemDetail): ItemDetail

    suspend fun updateItemDetail(itemDetail: ItemDetail): ItemDetail

    suspend fun deleteItemDetail(itemDetailId: String)


}