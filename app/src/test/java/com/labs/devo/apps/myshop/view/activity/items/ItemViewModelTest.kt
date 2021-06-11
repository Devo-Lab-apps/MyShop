package com.labs.devo.apps.myshop.view.activity.items

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.data.repo.ItemDetailRepositoryFakeImpl
import com.labs.devo.apps.myshop.data.repo.ItemRepositoryFakeImpl
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.data.util.ViewModelUtil.getFromPagingDataFlow
import com.labs.devo.apps.myshop.util.MainCoroutineRule
import com.labs.devo.apps.myshop.view.activity.items.item.ItemViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ItemViewModelTest {


    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    private lateinit var itemViewModel: ItemViewModel

    private lateinit var itemRepository: ItemRepository
    lateinit var itemDetailRepository: ItemDetailRepository

    @Before
    fun setup() {
        itemRepository = ItemRepositoryFakeImpl()
        itemDetailRepository = ItemDetailRepositoryFakeImpl()
        itemViewModel = ItemViewModel(itemRepository, itemDetailRepository)
    }


    @Test
    fun getItemsTest_returnsItems() = runBlocking {
        val itemDetail1 = ItemDetail(itemName = "Item 1", quantity = 2.0)
        val itemDetail2 = ItemDetail(itemName = "Item 2", quantity = 2.0)
        itemViewModel.createItemAndItemDetail(itemDetail1)
        itemViewModel.createItemAndItemDetail(itemDetail2)
        val itemsFlow = itemViewModel.items
        val data = getFromPagingDataFlow(itemsFlow)
        val expectedItems = listOf(itemDetail1, itemDetail2).map { Item(it.itemId) }
        assertEquals(expectedItems, data)
    }

    @Test
    fun createItemsTest_createsItemAndItemDetail() = runBlocking {

    }

    @Test
    fun updateItemTest_updateItemAndItemDetail() = runBlocking {

    }

    @Test
    fun deleteItemTest_deleteItemAndItemDetail() = runBlocking {

    }

}