package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PageViewModel @ViewModelInject constructor(
    val pageRepository: PageRepository
) : BaseViewModel<PageViewModel.PageEvent>() {


    fun onNotebookSelect() = viewModelScope.launch {
        channel.send(PageEvent.NavigateToNotebookFragment)
    }

    fun getPages(notebookId: String) = viewModelScope.launch {
        pageRepository.getPages(notebookId).collect { dataState ->
            dataState.data?.let {
                channel.send(
                    PageEvent.GetPagesEvent(
                        it.getContentIfNotHandled() ?: listOf()
                    )
                )
            }
                ?: channel.send(PageEvent.ShowInvalidInputMessage(dataState.message?.getContentIfNotHandled()))

        }
    }


    sealed class PageEvent {
        object NavigateToNotebookFragment : PageEvent()

        data class GetPagesEvent(val pages: List<Page>) : PageEvent()

        data class ShowInvalidInputMessage(val msg: String?) : PageEvent()
    }
}