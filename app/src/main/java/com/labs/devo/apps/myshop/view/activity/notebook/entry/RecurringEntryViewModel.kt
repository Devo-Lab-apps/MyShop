package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import com.labs.devo.apps.myshop.const.AppConstants.EMPTY_STRING
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class RecurringEntryViewModel
@ViewModelInject constructor(val entryRepository: EntryRepository) :
    BaseViewModel<RecurringEntryViewModel.RecurringEntryEvent>() {



    sealed class RecurringEntryEvent {
        object GetRecurringEvents : RecurringEntryEvent()
    }
}