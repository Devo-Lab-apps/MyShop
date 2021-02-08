package com.labs.devo.apps.myshop.view.activity.notebook.entry

import androidx.hilt.lifecycle.ViewModelInject
import com.labs.devo.apps.myshop.view.util.BaseViewModel

class EntryViewModel
    @ViewModelInject constructor()
    : BaseViewModel<EntryViewModel.EntryEvent>() {



    sealed class EntryEvent {

        data class ShowInvalidInputMessage(val msg: String?) : EntryEvent()

    }
}