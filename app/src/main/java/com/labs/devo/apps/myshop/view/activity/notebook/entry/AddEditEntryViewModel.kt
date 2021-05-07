package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.EntryMetadata
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ENTRY
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE_ID
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditEntryViewModel.AddEntryModelConstants.DIFFERENT_ENTRY_UPDATE
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditEntryViewModel.AddEntryModelConstants.ENTRY_DELETED
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditEntryViewModel.AddEntryModelConstants.ENTRY_INSERTED_MSG
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditEntryViewModel.AddEntryModelConstants.ENTRY_UPDATED
import com.labs.devo.apps.myshop.view.activity.notebook.entry.AddEditEntryViewModel.AddEntryModelConstants.SAME_ENTRY_ERR
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditEntryViewModel @ViewModelInject constructor(
    private val entryRepository: EntryRepository,
    private val recurringEntryRepository: RecurringEntryRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<AddEditEntryViewModel.AddEditEntryEvent>() {

    val entry = state.get<Entry>(ENTRY)

    val pageId = state.get<String>(PAGE_ID)!!

    val operation = state.get<String>(OPERATION)!!

    fun addEntry(entry: Entry) = viewModelScope.launch {
        if (entry.isRepeating) {
            val data = recurringEntry(entry)
            val res = recurringEntryRepository.insertRecurringEntry(data)
            res.data?.let {
                channel.send(AddEditEntryEvent.EntryInserted(ENTRY_INSERTED_MSG))
            }
                ?: channel.send(AddEditEntryEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
        } else {
            val res = entryRepository.insertEntry(entry)
            res.data?.let {
                channel.send(AddEditEntryEvent.EntryInserted(ENTRY_INSERTED_MSG))
            }
                ?: channel.send(AddEditEntryEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
        }


    }

    private fun recurringEntry(entry: Entry): RecurringEntry {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = FirebaseHelper.getRecurringEntryReference(user.accountId).id
        val metadata = entry.entryMetadata
        return RecurringEntry(
            pageId = entry.pageId,
            recurringEntryId = id,
            name = entry.entryTitle,
            description = entry.entryDescription,
            frequency = metadata[EntryMetadata.RECURRING_ENTRY_FREQUENCY]!!,
            recurringTime = metadata[EntryMetadata.RECURRING_ENTRY_TIME]!!,
            createdAt = entry.createdAt,
            modifiedAt = entry.modifiedAt,
            amount = entry.entryAmount
        )
    }

    fun updateEntry(prevEntry: Entry, entry: Entry) = viewModelScope.launch {
        if (prevEntry.entryId != entry.entryId) {
            channel.send(AddEditEntryEvent.ShowInvalidInputMessage(DIFFERENT_ENTRY_UPDATE))
            return@launch
        }
        if (entry.entryTitle == prevEntry.entryTitle && entry.entryAmount == prevEntry.entryAmount) {
            channel.send(AddEditEntryEvent.ShowInvalidInputMessage(SAME_ENTRY_ERR))
            return@launch
        }
        val data = entryRepository.updateEntry(entry)
        data.data?.let {
            channel.send(AddEditEntryEvent.EntryUpdated(ENTRY_UPDATED))
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
            channel.send(AddEditEntryEvent.EntryDeleted(ENTRY_DELETED))
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

    object AddEntryModelConstants {
        const val ENTRY_INSERTED_MSG = "Entry inserted"
        const val DIFFERENT_ENTRY_UPDATE = "Entry being updated is different"
        const val SAME_ENTRY_ERR = "Entry is not changed. Change to retry."
        const val ENTRY_UPDATED = "Entry is updated"
        const val ENTRY_DELETED = "Entry is deleted"
    }
}