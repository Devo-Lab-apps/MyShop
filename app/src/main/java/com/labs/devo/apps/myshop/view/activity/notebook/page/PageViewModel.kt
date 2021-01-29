package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class PageViewModel : BaseViewModel<PageViewModel.PageEvent>() {


    fun onNotebookSelect() = viewModelScope.launch {
        channel.send(PageEvent.NavigateToNotebookFragment)
    }


    sealed class PageEvent {
        object NavigateToNotebookFragment : PageEvent()
    }
}