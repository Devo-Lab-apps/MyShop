package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.const.AppConstants.EMPTY_STRING
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EntryViewModel
@ViewModelInject constructor(
    private val entryRepository: EntryRepository,
    @Assisted private val state: SavedStateHandle
) : BaseViewModel<EntryViewModel.EntryEvent>() {


    private val _searchQuery = state.getLiveData("entrySearchQuery", EMPTY_STRING)
    val searchQuery: LiveData<String> = _searchQuery

    private val _pageId = MutableStateFlow(EMPTY_STRING)
    val pageId: StateFlow<String> = _pageId

    private val _orderBy = MutableStateFlow(EMPTY_STRING)
    val orderBy: StateFlow<String> = _orderBy

    private var refreshStatus = false

    val entries = combine(
        _pageId, _searchQuery.asFlow(), _orderBy
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
        val pageId = _pageId.value
        _pageId.value = EMPTY_STRING
        refreshStatus = true
        _pageId.value = pageId
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

    fun addEntry() = viewModelScope.launch {
        channel.send(EntryEvent.AddEntryEvent)
    }

    fun onEntryClick(entry: Entry) = viewModelScope.launch {
        channel.send(EntryEvent.EditEntryEvent(entry))
    }


    sealed class EntryEvent {

        data class ShowInvalidInputMessage(val msg: String?) : EntryEvent()
        object AddEntryEvent : EntryEvent()
        data class EditEntryEvent(val entry: Entry) : EntryEvent()

    }
}