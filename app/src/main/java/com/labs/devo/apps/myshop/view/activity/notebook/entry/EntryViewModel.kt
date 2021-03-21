package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class EntryViewModel
@ViewModelInject constructor(private val entryRepository: EntryRepository) :
    BaseViewModel<EntryViewModel.EntryEvent>() {


    private val _searchQuery = MutableStateFlow("")

    private val _pageId = MutableStateFlow("")

    private val _orderBy = MutableStateFlow("")

    private var refreshStatus = false

    val entries = combine(
        _pageId, _searchQuery, _orderBy
    ) { _pageId, _searchQuery, _orderBy ->
        Triple(_pageId, _searchQuery, _orderBy)
    }.flatMapLatest { (pageId, searchQuery, orderBy) ->
        if (pageId.isEmpty()) {
            emptyFlow()
        } else {
            val data = entryRepository.getEntries(
                pageId,
                searchQuery,
                orderBy,
                refreshStatus
            )
            refreshStatus = false
            data
        }
    }.cachedIn(viewModelScope)


    private suspend fun handleGetEntries(dataState: DataState<List<Entry>>) {
//        dataState.data?.let { event ->
//            val entries = event.getContentIfNotHandled()
//            if (entries.isNullOrEmpty()) {
//                dataState.message = Event.messageEvent("No entries in this page.")
//            }
//            channel.send(
//                EntryEvent.GetEntries(
//                    entries ?: listOf(),
//                    dataState
//                )
//            )
//        }
//            ?: channel.send(EntryEvent.ShowInvalidInputMessage(dataState.message?.getContentIfNotHandled()))
    }

    fun syncEntries() {
//        val pageId = _pageId.value
//        _pageId.value = ""
        refreshStatus = true
        _pageId.value = _pageId.value
    }

    fun setPageId(pageId: String) {
        _pageId.value = pageId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setOrderBy(col: String) {
        _orderBy.value = col
    }


    sealed class EntryEvent {

        data class ShowInvalidInputMessage(val msg: String?) : EntryEvent()

    }
}