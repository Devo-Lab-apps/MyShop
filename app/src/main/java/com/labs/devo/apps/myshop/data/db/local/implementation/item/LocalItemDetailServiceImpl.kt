package com.labs.devo.apps.myshop.data.db.local.implementation.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemDetailService
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDetailDao
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalItemDetailServiceImpl
@Inject constructor(private val itemDetailDao: ItemDetailDao) : LocalItemDetailService {

    override fun getItemDetails(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, ItemDetail> {
        return itemDetailDao.getItemDetails()
    }

    override suspend fun createItemDetail(itemDetail: ItemDetail) {
        AsyncHelper.runAsync {
            itemDetailDao.createItemDetail(itemDetail)
        }
    }

    override suspend fun updateItemDetail(itemDetail: ItemDetail) {
        AsyncHelper.runAsync {
            itemDetailDao.updateItemDetail(itemDetail)
        }
    }

    override suspend fun deleteItemDetail(itemId: String) {
        AsyncHelper.runAsync {
            itemDetailDao.deleteItemDetail(itemId)
        }
    }

    override suspend fun deleteItemDetails() {
        AsyncHelper.runAsync {
            itemDetailDao.deleteItemDetails()
        }
    }
}