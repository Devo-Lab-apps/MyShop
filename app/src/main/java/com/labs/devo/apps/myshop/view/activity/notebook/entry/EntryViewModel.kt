package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.Event
import com.labs.devo.apps.myshop.view.util.QueryParams
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EntryViewModel
@ViewModelInject constructor(val entryRepository: EntryRepository) :
    BaseViewModel<EntryViewModel.EntryEvent>() {


    fun getEntries(pageId: String, queryParams: QueryParams) = viewModelScope.launch {
        entryRepository.getEntries(pageId, queryParams).collect { dataState ->
            handleGetEntries(dataState)
        }
    }

    private suspend fun handleGetEntries(dataState: DataState<List<Entry>>) {
        dataState.data?.let { event ->
            val entries = event.getContentIfNotHandled()
            if (entries.isNullOrEmpty()) {
                dataState.message = Event.messageEvent("No entries in this page.")
            }
            channel.send(
                EntryEvent.GetEntries(
                    entries ?: listOf(),
                    dataState
                )
            )
        }
            ?: channel.send(EntryEvent.ShowInvalidInputMessage(dataState.message?.getContentIfNotHandled()))
    }

    fun syncEntries(pageId: String) = viewModelScope.launch {
        try {
            val dataState = entryRepository.syncEntries(pageId)
            handleGetEntries(dataState)
        } catch (ex: Exception) {
            channel.send(
                EntryEvent.ShowInvalidInputMessage(
                    ex.message ?: "Can't sync pages. Please try again."
                )
            )
        }
    }


    sealed class EntryEvent {

        data class ShowInvalidInputMessage(val msg: String?) : EntryEvent()

        data class GetEntries(val entries: List<Entry>, val dataState: DataState<List<Entry>>) :
            EntryEvent()

    }
}