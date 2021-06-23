package com.labs.devo.apps.myshop.view.activity.items.item

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class ItemDetailViewModel @ViewModelInject constructor(
    private val itemRepository: ItemRepository,
    private val itemDetailRepository: ItemDetailRepository,
    @Assisted val state: SavedStateHandle
) : BaseViewModel<ItemDetailViewModel.ItemDetailEvent>() {

    val item = state.get<Item>(ItemActivity.ItemConstants.ITEM)

    lateinit var itemDetail: ItemDetail

    fun getItemDetail() = viewModelScope.launch {
        if (item == null) {
            channel.send(ItemDetailEvent.InvalidItem)
            return@launch
        }
        val itemDetailRes = itemDetailRepository.getItemDetail(item.itemId)
        itemDetailRes.data?.let {
            val res = it.getContentIfNotHandled()
            if (res != null) {
                itemDetail = res
                channel.send(ItemDetailEvent.ItemDetailFound)
            } else channel.send(ItemDetailEvent.InvalidItem)
        } ?: run {
            channel.send(ItemDetailEvent.InvalidItem)
        }
    }

    fun editItem() = viewModelScope.launch {
        channel.send(ItemDetailEvent.EditItemEvent)
    }

    sealed class ItemDetailEvent {

        object EditItemEvent : ItemDetailEvent()

        object ItemDetailFound : ItemDetailEvent()

        object InvalidItem : ItemDetailEvent()
    }
}