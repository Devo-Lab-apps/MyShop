package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.MicroEntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MicroEntryViewModel
@ViewModelInject constructor(
    private val microEntryRepository: MicroEntryRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<MicroEntryViewModel.MicroEntryEvent>() {

    private val _searchQuery = state.getLiveData("entrySearchQuery", AppConstants.EMPTY_STRING)
    val searchQuery: LiveData<String> = _searchQuery

    private val _pageId = MutableStateFlow(AppConstants.EMPTY_STRING)
    val pageId: StateFlow<String> = _pageId

    private val _orderBy = MutableStateFlow(AppConstants.EMPTY_STRING)
    val orderBy: StateFlow<String> = _orderBy

    private var refreshStatus = false

    private lateinit var recurringEntry: RecurringEntry

    fun getMicroEntries(recurringEntry: RecurringEntry): Flow<PagingData<Entry>> {
        return combine(
            _searchQuery.asFlow(), _orderBy
        ) { _searchQuery, _orderBy ->
            Pair(_searchQuery, _orderBy)
        }.flatMapLatest { (searchQuery, orderBy) ->
            if (recurringEntry.pageId.isEmpty()) {
                emptyFlow()
            } else {
                val data = microEntryRepository.getMicroEntries(
                    recurringEntry.pageId,
                    recurringEntry,
                    searchQuery,
                    orderBy,
                    refreshStatus
                )
                refreshStatus = false
                data
            }
        }.cachedIn(viewModelScope)
    }


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

    fun syncMicroEntries() {
        val pageId = _pageId.value
        _pageId.value = AppConstants.EMPTY_STRING
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

    fun addMicroEntry() = viewModelScope.launch {
        channel.send(MicroEntryEvent.AddMicroEntryEvent)
    }

    fun onEntryClick(entry: Entry) = viewModelScope.launch {
        channel.send(MicroEntryEvent.EditMicroEntryEvent(entry))
    }

    sealed class MicroEntryEvent {
        object AddMicroEntryEvent : MicroEntryViewModel.MicroEntryEvent()

        data class ShowInvalidInputMessage(val msg: String?) : MicroEntryEvent()

        data class EditMicroEntryEvent(val entry: Entry) : MicroEntryEvent()
    }
}