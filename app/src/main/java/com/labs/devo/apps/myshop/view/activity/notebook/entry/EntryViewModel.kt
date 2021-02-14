package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EntryViewModel
@ViewModelInject constructor(val entryRepository: EntryRepository) :
    BaseViewModel<EntryViewModel.EntryEvent>() {


    fun getEntries(pageId: String) = viewModelScope.launch {
        entryRepository.getEntries(pageId).collect { dataState ->
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


    sealed class EntryEvent {

        data class ShowInvalidInputMessage(val msg: String?) : EntryEvent()

        data class GetEntries(val entries: List<Entry>, val dataState: DataState<List<Entry>>) :
            EntryEvent()

    }
}