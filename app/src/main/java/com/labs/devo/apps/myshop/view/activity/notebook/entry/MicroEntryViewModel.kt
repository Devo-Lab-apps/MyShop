package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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

    private val _dateRange = MutableStateFlow(Pair(0L, Long.MAX_VALUE))
    val dateRange: StateFlow<Pair<Long, Long>> = _dateRange

    private val _orderBy = MutableStateFlow(AppConstants.EMPTY_STRING)
    val orderBy: StateFlow<String> = _orderBy

    private val _recurringEntry = MutableStateFlow(RecurringEntry())
    val recurringEntry: StateFlow<RecurringEntry> = _recurringEntry

    private var refreshStatus = false

    val entries = combine(
        _recurringEntry, _dateRange, _orderBy
    ) { _recurringEntry, _searchQuery, _orderBy ->
        Triple(_recurringEntry, _searchQuery, _orderBy)
    }.flatMapLatest { (recurringEntry, searchQuery, orderBy) ->
        if (recurringEntry.recurringEntryId.isEmpty()) {
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

    fun setRecurringEntry(e: RecurringEntry) {
        _recurringEntry.value = e
    }

    fun syncMicroEntries() {
        val recurringEntryTemp = _recurringEntry.value
        _recurringEntry.value = RecurringEntry()
        refreshStatus = true
        _recurringEntry.value = recurringEntryTemp
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

    fun selectDate(start: Long, end: Long) {
        _dateRange.value = Pair(start, end)
    }

    sealed class MicroEntryEvent {
        object AddMicroEntryEvent : MicroEntryViewModel.MicroEntryEvent()

        data class ShowInvalidInputMessage(val msg: String?) : MicroEntryEvent()

        data class EditMicroEntryEvent(val entry: Entry) : MicroEntryEvent()
    }
}