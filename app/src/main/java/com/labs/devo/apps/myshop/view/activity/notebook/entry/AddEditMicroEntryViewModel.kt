package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.EntryMetadata
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.MicroEntryRepository
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditMicroEntryViewModel.AddMicroEntryModelConstants.DIFFERENT_MICRO_ENTRY_UPDATE
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditMicroEntryViewModel.AddMicroEntryModelConstants.MICRO_ENTRY_DELETED
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditMicroEntryViewModel.AddMicroEntryModelConstants.MICRO_ENTRY_INSERTED_MSG
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditMicroEntryViewModel.AddMicroEntryModelConstants.MICRO_ENTRY_UPDATED
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditMicroEntryViewModel.AddMicroEntryModelConstants.SAME_MICRO_ENTRY_ERR
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditMicroEntryViewModel @ViewModelInject constructor(
    private val microEntryRepository: MicroEntryRepository
) :
    BaseViewModel<AddEditMicroEntryViewModel.AddEditMicroEntryEvent>() {


    fun addMicroEntry(recurringEntry: RecurringEntry, entry: Entry) = viewModelScope.launch {

        val res = microEntryRepository.insertMicroEntry(recurringEntry, entry)
        res.data?.let {
            channel.send(AddEditMicroEntryEvent.MicroEntryInserted(MICRO_ENTRY_INSERTED_MSG))
        }
            ?: channel.send(AddEditMicroEntryEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updateEntry(recurringEntry: RecurringEntry, prevEntry: Entry, entry: Entry) =
        viewModelScope.launch {
            if (prevEntry.entryId != entry.entryId) {
                channel.send(
                    AddEditMicroEntryEvent.ShowInvalidInputMessage(
                        DIFFERENT_MICRO_ENTRY_UPDATE
                    )
                )
                return@launch
            }
            if (entry.entryTitle == prevEntry.entryTitle && entry.entryAmount == prevEntry.entryAmount) {
                channel.send(AddEditMicroEntryEvent.ShowInvalidInputMessage(SAME_MICRO_ENTRY_ERR))
                return@launch
            }
            val data = microEntryRepository.updateMicroEntry(recurringEntry, entry)
            data.data?.let {
                channel.send(AddEditMicroEntryEvent.MicroEntryUpdated(MICRO_ENTRY_UPDATED))
            }
                ?: channel.send(
                    AddEditMicroEntryEvent.ShowInvalidInputMessage(
                        data.message?.getContentIfNotHandled()
                    )
                )
        }

    fun deleteEntry(recurringEntry: RecurringEntry, entry: Entry) = viewModelScope.launch {
        val data = microEntryRepository.deleteMicroEntry(recurringEntry, entry)
        data.data?.let {
            channel.send(AddEditMicroEntryEvent.MicroEntryDeleted(MICRO_ENTRY_DELETED))
        }
            ?: channel.send(
                AddEditMicroEntryEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    sealed class AddEditMicroEntryEvent {
        data class ShowInvalidInputMessage(val msg: String?) : AddEditMicroEntryEvent()

        data class MicroEntryInserted(val msg: String) : AddEditMicroEntryEvent()

        data class MicroEntryUpdated(val msg: String) : AddEditMicroEntryEvent()

        data class MicroEntryDeleted(val msg: String) : AddEditMicroEntryEvent()
    }

    object AddMicroEntryModelConstants {
        const val MICRO_ENTRY_INSERTED_MSG = "Entry inserted"
        const val DIFFERENT_MICRO_ENTRY_UPDATE = "Entry being updated is different"
        const val SAME_MICRO_ENTRY_ERR = "Entry is not changed. Change to retry."
        const val MICRO_ENTRY_UPDATED = "Entry is updated"
        const val MICRO_ENTRY_DELETED = "Entry is deleted"
    }
}