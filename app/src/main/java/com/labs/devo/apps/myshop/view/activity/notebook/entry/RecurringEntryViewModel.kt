package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE_ID
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class RecurringEntryViewModel
@ViewModelInject constructor(
    private val recurringEntryRepository: RecurringEntryRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<RecurringEntryViewModel.RecurringEntryEvent>() {

    private val _pageId = state.getLiveData<String>(PAGE_ID)

    val recurringEntries = _pageId.asFlow().flatMapLatest { pageId ->
        val data = recurringEntryRepository.getRecurringEntries(
            pageId,
            false
        )
        data
    }.cachedIn(viewModelScope)



    sealed class RecurringEntryEvent {
        data class ShowInvalidInputMessage(val msg: String?) : RecurringEntryEvent()
    }
}