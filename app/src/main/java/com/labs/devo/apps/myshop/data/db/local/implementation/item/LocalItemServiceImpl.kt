package com.labs.devo.apps.myshop.data.db.local.implementation.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemService
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDao
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalItemServiceImpl
@Inject constructor(private val itemDao: ItemDao) : LocalItemService {

    override fun getLocalItems(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Item> {
        return itemDao.getItems()
    }

    override suspend fun createLocalItem(item: Item) {
        AsyncHelper.runAsync {
            itemDao.insertItem(item)
        }
    }

    override suspend fun updateItem(item: Item) {
        AsyncHelper.runAsync {
            itemDao.updateItem(item)
        }
    }

    override suspend fun deleteItem(itemId: String) {
        AsyncHelper.runAsync {
            itemDao.deleteItem(itemId)
        }
    }

    override suspend fun deleteItems() {
        AsyncHelper.runAsync {
            itemDao.deleteItems()
        }
    }
}