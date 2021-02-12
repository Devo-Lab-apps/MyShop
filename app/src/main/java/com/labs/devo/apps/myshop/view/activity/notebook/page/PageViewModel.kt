package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PageViewModel @ViewModelInject constructor(
    val pageRepository: PageRepository
) : BaseViewModel<PageViewModel.PageEvent>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    fun onNotebookSelect() = viewModelScope.launch {
        channel.send(PageEvent.NavigateToNotebookFragment)
    }

    fun getPages(notebookId: String) = viewModelScope.launch {
        try {
            pageRepository.getPages(notebookId).collect { dataState ->
                handleGetPages(dataState)
            }
        } catch (ex: Exception) {
            channel.send(
                PageEvent.ShowInvalidInputMessage(
                    ex.message ?: "Can't fetch the pages. Please try again."
                )
            )
        }
    }

    fun syncPages(notebookId: String) = viewModelScope.launch {
        try {
            val dataState = pageRepository.syncPages(notebookId)
            handleGetPages(dataState)
        } catch (ex: Exception) {
            channel.send(
                PageEvent.ShowInvalidInputMessage(
                    ex.message ?: "Can't sync pages. Please try again."
                )
            )
        }
    }

    private suspend fun handleGetPages(dataState: DataState<List<Page>>) {
        dataState.data?.let {
            channel.send(
                PageEvent.GetPagesEvent(
                    it.getContentIfNotHandled() ?: listOf(),
                    dataState
                )
            )
        }
            ?: channel.send(
                PageEvent.GetPagesEvent(
                    listOf(),
                    dataState
                )
            )
    }


    sealed class PageEvent {
        object NavigateToNotebookFragment : PageEvent()

        data class GetPagesEvent(val pages: List<Page>, val dataState: DataState<List<Page>>) :
            PageEvent()

        data class ShowInvalidInputMessage(val msg: String?) : PageEvent()
    }
}