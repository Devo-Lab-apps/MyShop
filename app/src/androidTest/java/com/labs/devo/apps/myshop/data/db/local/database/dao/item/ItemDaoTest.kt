package com.labs.devo.apps.myshop.data.db.local.database.dao.item

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.util.DaoUtil.getPagingSource
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.models.item.Item
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ItemDaoTest {
    private lateinit var database: ItemDatabase
    private lateinit var dao: ItemDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.itemDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun createItemTest_createItem() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        val data = getPagingSource(dao.getItems())

        assertEquals(listOf(item), data)
    }

    @Test
    fun getItemTest_getItem() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        val data = dao.getItem("1")
        assertEquals(item, data)
    }

    @Test
    fun getItemTest_noItem() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        val data = dao.getItem("52")
        assertEquals(null, data)
    }

    @Test
    fun deleteItemTest_deletesItem() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        dao.deleteItem("1")
        val data = getPagingSource(dao.getItems())
        assertEquals(listOf<Item>(), data)
    }

    @Test
    fun updateItemTest_updatesItem() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        val updatedItem = item.copy(quantity = 20.0)
        dao.updateItem(updatedItem)
        val data = getPagingSource(dao.getItems())
        assertEquals(listOf(updatedItem), data)
    }

    @Test
    fun updateNonExistingItemTest_wontDoAnything() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        dao.createItem(item)
        val updatedItem = item.copy(itemId = "2", quantity = 20.0)
        dao.updateItem(updatedItem)
        val data = getPagingSource(dao.getItems())
        assertEquals(listOf(item), data)
    }

    @Test
    fun deleteAllItemTest_deleteAllItems() = runBlocking {
        val item = Item("1", "Bat", 10.0)
        val item2 = Item("2", "Ball", 10.0)
        dao.createItem(item)
        dao.createItem(item2)
        dao.deleteItems()
        val data = getPagingSource(dao.getItems())
        assertEquals(listOf<Item>(), data)
    }

    @Test
    fun getLastFetchedItemWithItemsTest_returnsLastFetchedItem() = runBlocking {
        val item1 = Item("1", "Bat", 10.0, modifiedAt = 10)
        val item2 = Item("1", "Bat", 10.0, modifiedAt = 20)
        dao.createItem(item1)
        dao.createItem(item2)
        val data = dao.getLastFetchedItem()
        assertEquals(item2, data)
    }

    @Test
    fun getLastFetchedItemWithoutItemsTest_returnsNull() = runBlocking {
        val data = dao.getLastFetchedItem()
        //because no item in the database
        assertEquals(null, data)
    }
}