package com.labs.devo.apps.myshop.data.db.local.implementation.item

import androidx.paging.PagingSource
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemService
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDao
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.AsyncHelper
import javax.inject.Inject

class LocalItemServiceImpl
@Inject constructor(private val itemDao: ItemDao) : LocalItemService {

    override fun getItems(
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Item> {
        var finalOrderBy = orderBy
        if (orderBy.isEmpty()) {
            finalOrderBy = Item::itemName.name
        }
        val s = "%$searchQuery%"
        return itemDao.getItems(s, finalOrderBy)
    }

    override suspend fun createItem(item: Item) {
        AsyncHelper.runAsync {
            itemDao.createItem(item)
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