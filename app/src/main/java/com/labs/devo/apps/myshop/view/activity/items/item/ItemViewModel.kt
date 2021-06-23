package com.labs.devo.apps.myshop.view.activity.items.item

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemDetailRepository
import com.labs.devo.apps.myshop.data.repo.item.abstraction.ItemRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class ItemViewModel @ViewModelInject constructor(
    private val itemRepository: ItemRepository,
    private val itemDetailRepository: ItemDetailRepository
) : BaseViewModel<ItemViewModel.ItemEvent>() {


    private val _searchQuery = MutableLiveData(AppConstants.EMPTY_STRING)
    val searchQuery: LiveData<String> = _searchQuery

    private val _orderBy = MutableStateFlow(AppConstants.EMPTY_STRING)
    val orderBy: StateFlow<String> = _orderBy

    private var refreshStatus = false

    val items = combine(
        _searchQuery.asFlow(), _orderBy
    ) { searchQuery, orderBy ->
        Pair(searchQuery, orderBy)
    }.flatMapLatest { (searchQuery, orderBy) ->
        val data = itemRepository.getItems(
            searchQuery,
            orderBy,
            refreshStatus
        )
        refreshStatus = false
        data
    }.cachedIn(viewModelScope)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setOrderBy(colName: String) {
        _orderBy.value = colName
    }

    fun createItem() = viewModelScope.launch {
        channel.send(ItemEvent.CreateItemEvent)
    }

    fun syncItems() {
        //TODO trigger refresh
        refreshStatus = true
    }

    sealed class ItemEvent {
        data class ShowInvalidInputMessage(val msg: String?) : ItemEvent()

        object CreateItemEvent : ItemEvent()

    }

}