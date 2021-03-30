package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.view.util.BaseViewModel

class MicroEntryViewModel
@ViewModelInject constructor() :
    BaseViewModel<MicroEntryViewModel.MicroEntryEvent>() {


    sealed class MicroEntryEvent {
        data class GetMicroEntriesEvent(val entries: List<Entry>) :
            MicroEntryEvent()

        data class ShowInvalidInputMessage(val msg: String?) : MicroEntryEvent()
    }
}