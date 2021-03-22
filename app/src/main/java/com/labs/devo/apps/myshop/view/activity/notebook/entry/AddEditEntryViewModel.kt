package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditEntryViewModel @ViewModelInject constructor(private val entryRepository: EntryRepository) :
    BaseViewModel<AddEditEntryViewModel.AddEditEntryEvent>() {


    fun addEntry(entry: Entry) = viewModelScope.launch {
        val res = entryRepository.insertEntry(entry)
        res.data?.let {
            channel.send(AddEditEntryEvent.EntryInserted("Entry inserted"))
        }
            ?: channel.send(AddEditEntryEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updateEntry(prevEntry: Entry, entry: Entry) = viewModelScope.launch {
        if (prevEntry.entryId != entry.entryId) {
            channel.send(AddEditEntryEvent.ShowInvalidInputMessage("Entry being updated is different"))
            return@launch
        }
        if (entry.entryTitle == prevEntry.entryTitle) {
            channel.send(AddEditEntryEvent.ShowInvalidInputMessage("Entry's title is not changed"))
            return@launch
        }
        val data = entryRepository.updateEntry(entry)
        data.data?.let {
            channel.send(AddEditEntryEvent.EntryUpdated("Entry is updated"))
        }
            ?: channel.send(
                AddEditEntryEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    fun deleteEntry(entry: Entry) = viewModelScope.launch {
        val data = entryRepository.deleteEntry(entry)
        data.data?.let {
            channel.send(AddEditEntryEvent.EntryDeleted("Entry is deleted"))
        }
            ?: channel.send(
                AddEditEntryEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    sealed class AddEditEntryEvent {
        data class ShowInvalidInputMessage(val msg: String?) : AddEditEntryEvent()

        data class EntryInserted(val msg: String) : AddEditEntryEvent()

        data class EntryUpdated(val msg: String) : AddEditEntryEvent()

        data class EntryDeleted(val msg: String) : AddEditEntryEvent()
    }
}