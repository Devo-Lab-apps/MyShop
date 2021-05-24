package com.labs.devo.apps.myshop.data.db.local.database.dao.item

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.util.DaoUtil.getPagingSource
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ItemDetailDaoTest {
    private lateinit var database: ItemDatabase
    private lateinit var dao: ItemDetailDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.itemDetailDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun createItemDetailTest_createItemDetail() = runBlocking {
        val itemDetail = ItemDetail("1", "1", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        val data = getPagingSource(dao.getItemDetails())

        assertEquals(listOf(itemDetail), data)
    }

    @Test
    fun getItemDetailByIdTest_getItemDetailById() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        val data = dao.getItemDetailById("1")
        assertEquals(itemDetail, data)
    }

    @Test
    fun getItemDetailTest_getItemDetail() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        val data = dao.getItemDetail("51")
        assertEquals(itemDetail, data)
    }

    @Test
    fun updateNonExistingItemDetailTest_wontDoAnything() = runBlocking {
        val item = ItemDetail("1", "51", "Bat", 10.0)
        dao.createItemDetail(item)
        val updatedItem = item.copy(itemDetailId = "2", quantity = 20.0)
        dao.updateItemDetail(updatedItem)
        val data = getPagingSource(dao.getItemDetails())
        assertEquals(listOf(item), data)
    }

    @Test
    fun getItemDetailTest_noItemDetail() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        val data = dao.getItemDetailById("52")
        assertEquals(null, data)
    }

    @Test
    fun deleteItemDetailTest_deletesItemDetail() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        dao.deleteItemDetailById("1")
        val data = getPagingSource(dao.getItemDetails())
        assertEquals(listOf<ItemDetail>(), data)
    }

    @Test
    fun updateItemDetailTest_updatesItemDetail() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        val updatedItemDetail = itemDetail.copy(quantity = 20.0)
        dao.updateItemDetail(updatedItemDetail)
        val data = getPagingSource(dao.getItemDetails())
        assertEquals(listOf(updatedItemDetail), data)
    }

    @Test
    fun deleteAllItemDetailTest_deleteAllItems() = runBlocking {
        val itemDetail = ItemDetail("1", "51", "Bat", 10.0, "To play cricket")
        val itemDetail2 = ItemDetail("2", "52", "Bat", 10.0, "To play cricket")
        dao.createItemDetail(itemDetail)
        dao.createItemDetail(itemDetail2)
        dao.deleteItemDetails()
        val data = getPagingSource(dao.getItemDetails())
        assertEquals(listOf<ItemDetail>(), data)
    }

    @Test
    fun getLastFetchedItemDetailTest_returnsLastFetchedItem() = runBlocking {
        val itemDetail1 = ItemDetail("1", "51", "Bat", 10.0, "To play cricket", modifiedAt = 10)
        val itemDetail2 = ItemDetail("1", "51", "Bat", 10.0, "To play cricket", modifiedAt = 20)
        dao.createItemDetail(itemDetail1)
        dao.createItemDetail(itemDetail2)
        val data = dao.getLastFetchedItemDetail()
        assertEquals(itemDetail2, data)
    }

    @Test
    fun getLastFetchedItemDetailWithoutItemsTest_returnsNull() = runBlocking {
        val data = dao.getLastFetchedItemDetail()
        //because no item in the database
        assertEquals(null, data)
    }
}