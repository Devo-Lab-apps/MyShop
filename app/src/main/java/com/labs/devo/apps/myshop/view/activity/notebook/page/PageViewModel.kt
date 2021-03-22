package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class PageViewModel @ViewModelInject constructor(
    val pageRepository: PageRepository,
    val preferencesManager: PreferencesManager
) : BaseViewModel<PageViewModel.PageEvent>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val _searchQuery = MutableStateFlow("")

    private val _notebookId = MutableStateFlow("")

    private val _orderBy = MutableStateFlow("")

    private var refreshStatus = false

    val pages = combine(
        _notebookId, _searchQuery, _orderBy
    ) { notebookId, searchQuery, orderBy ->
        Triple(notebookId, searchQuery, orderBy)
    }.flatMapLatest { (notebookId, searchQuery, orderBy) ->
        if (notebookId.isEmpty()) {
            emptyFlow()
        } else {
            val data = pageRepository.getPages(
                notebookId,
                searchQuery,
                orderBy,
                refreshStatus
            )
            refreshStatus = false
            data
        }
    }.cachedIn(viewModelScope)

    fun onNotebookSelect() = viewModelScope.launch {
        channel.send(PageEvent.NavigateToNotebookFragment)
    }

    fun syncPages() {
//        val notebookId = _notebookId.value
//        _notebookId.value = ""
        refreshStatus = true
        _notebookId.value = _notebookId.value
    }

    private suspend fun handleGetPages(dataState: DataState<List<Page>>) {
        dataState.data?.let { event ->
            val pages = event.getContentIfNotHandled()
            if (pages.isNullOrEmpty()) {
                dataState.message = Event.messageEvent("No pages in this notebook.")
            }
            channel.send(
                PageEvent.GetPagesEvent(
                    pages ?: listOf(),
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

    fun setNotebookId(notebookId: String) {
        _notebookId.value = notebookId
    }

    fun setSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    fun setOrderBy(col: String) {
        _orderBy.value = col
    }


    sealed class PageEvent {
        object NavigateToNotebookFragment : PageEvent()

        data class GetPagesEvent(val pages: List<Page>, val dataState: DataState<List<Page>>) :
            PageEvent()

        data class ShowInvalidInputMessage(val msg: String?) : PageEvent()
    }
}