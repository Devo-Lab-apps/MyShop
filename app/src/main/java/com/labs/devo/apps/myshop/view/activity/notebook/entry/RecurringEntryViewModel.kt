package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class RecurringEntryViewModel
@ViewModelInject constructor(private val recurringEntryRepository: RecurringEntryRepository) :
    BaseViewModel<RecurringEntryViewModel.RecurringEntryEvent>() {


    fun getRecurringEntries(pageId: String) = viewModelScope.launch {
        val dataState = recurringEntryRepository.getRecurringEntries(pageId)
        dataState.data?.let {
            val entries = it.getContentIfNotHandled()
            channel.send(RecurringEntryEvent.GetRecurringEntriesEvent(entries ?: listOf()))
        } ?: channel.send(
            RecurringEntryEvent.ShowInvalidInputMessage(
                dataState.message?.getContentIfNotHandled()
            )
        )
    }

    fun onRecurringEntryClick(recurringEntry: RecurringEntry) = viewModelScope.launch {
        channel.send(RecurringEntryEvent.NavigateToMicroEntryFragment(recurringEntry))
    }


    sealed class RecurringEntryEvent {
        data class GetRecurringEntriesEvent(val entries: List<RecurringEntry>) :
            RecurringEntryEvent()

        data class ShowInvalidInputMessage(val msg: String?) : RecurringEntryEvent()

        data class NavigateToMicroEntryFragment(val entry: RecurringEntry) : RecurringEntryEvent()
    }
}