package com.labs.devo.apps.myshop.view.activity.items.item

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.view.activity.items.item.AddEditItemViewModel.AddItemModelConstants.ITEM_DELETED
import com.labs.devo.apps.myshop.view.activity.items.item.AddEditItemViewModel.AddItemModelConstants.ITEM_INSERTED_MSG
import com.labs.devo.apps.myshop.view.activity.items.item.AddEditItemViewModel.AddItemModelConstants.ITEM_UPDATED
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditItemViewModel @ViewModelInject constructor(
    private val itemRepository: ItemRepository,
    private val itemDetailRepository: ItemDetailRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<AddEditItemViewModel.AddEditItemEvent>() {

    val item = state.get<Item>(ItemActivity.ItemConstants.ITEM)

    val itemDetail = state.get<ItemDetail>(ItemActivity.ItemConstants.ITEM_DETAIL)

    val operation = state.get<String>(NotebookActivity.NotebookConstants.OPERATION)!!

    fun createItem(itemDetail: ItemDetail) = viewModelScope.launch {
        //TODO add validation here.
        val item = convertToItem(itemDetail)
        val itemRes = itemRepository.createItem(item)
        itemRes.data?.let { event ->
            val resItem = event.getContentIfNotHandled()!!
            val itemDetailData = itemDetail.copy(itemId = resItem.itemId)
            val itemDetailRes = itemDetailRepository.createItemDetail(itemDetailData)
            itemDetailRes.data?.let {
                channel.send(AddEditItemEvent.ItemInserted(ITEM_INSERTED_MSG))
            } ?: run {
                event.getContentIfNotHandled()?.let {
                    itemRepository.deleteItem(resItem)
                }
                showError(itemDetailRes.message?.getContentIfNotHandled())
            }
        } ?: run {
            showError(itemRes.message?.getContentIfNotHandled())
        }
    }

    private suspend fun showError(str: String?) {
        channel.send(
            AddEditItemEvent.ShowInvalidInputMessage(
                str ?: UNKNOWN_ERROR_OCCURRED_RETRY
            )
        )
    }

    private fun convertToItem(itemDetail: ItemDetail): Item {
        return Item(
            itemDetail.itemId,
            itemDetail.itemName,
            itemDetail.quantity,
            itemDetail.imageUrl
        )
    }


    fun updateItem(prevItemDetail: ItemDetail, itemDetail: ItemDetail) = viewModelScope.launch {
        //TODO add validation here
        val item = convertToItem(itemDetail)
        val itemRes = itemRepository.updateItem(item)
        itemRes.data?.let {
            val itemDetailRes = itemDetailRepository.updateItemDetail(itemDetail)
            itemDetailRes.data?.let {
                channel.send(AddEditItemEvent.ItemUpdated(ITEM_UPDATED))
            } ?: run {
                val prevItem = convertToItem(prevItemDetail)
                itemRepository.updateItem(prevItem)
                showError(itemDetailRes.message?.getContentIfNotHandled())
            }
        } ?: showError(itemRes.message?.getContentIfNotHandled())
    }

    fun deleteItem(itemDetail: ItemDetail) = viewModelScope.launch {
        val itemRes = itemRepository.deleteItem(convertToItem(itemDetail))
        itemRes.data?.let {
            val itemDetailRes = itemDetailRepository.deleteItemDetail(itemDetail)
            itemDetailRes.data?.let {
                channel.send(AddEditItemEvent.ItemDeleted(ITEM_DELETED))
            } ?: showError(itemDetailRes.message?.getContentIfNotHandled())
        } ?: showError(itemRes.message?.getContentIfNotHandled())
    }

    sealed class AddEditItemEvent {
        data class ShowInvalidInputMessage(val msg: String?) : AddEditItemEvent()

        data class ItemInserted(val msg: String) : AddEditItemEvent()

        data class ItemUpdated(val msg: String) : AddEditItemEvent()

        data class ItemDeleted(val msg: String) : AddEditItemEvent()

    }

    object AddItemModelConstants {
        const val ITEM_INSERTED_MSG = "Item inserted"
        const val DIFFERENT_ITEM_UPDATE = "Item being updated is different"
        const val SAME_ITEM_ERR = "Item is not changed. Change to retry."
        const val ITEM_UPDATED = "Item is updated"
        const val ITEM_DELETED = "Item is deleted"
    }
}