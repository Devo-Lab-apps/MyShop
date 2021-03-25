package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.AppConstants.EMPTY_STRING
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PageViewModel @ViewModelInject constructor(
    private val pageRepository: PageRepository,
    val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : BaseViewModel<PageViewModel.PageEvent>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val _searchQuery = state.getLiveData("pageSearchQuery", EMPTY_STRING)
    val searchQuery: LiveData<String> = _searchQuery

    private val _notebookId = MutableStateFlow(EMPTY_STRING)
    val notebookId: StateFlow<String> = _notebookId

    private val _orderBy = MutableStateFlow(EMPTY_STRING)
    val orderBy: StateFlow<String> = _orderBy

    private var refreshStatus = false

    val pages = combine(
        _notebookId, _searchQuery.asFlow(), _orderBy
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
        val notebookId = _notebookId.value
        _notebookId.value = EMPTY_STRING
        refreshStatus = true
        _notebookId.value = notebookId
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

        data class ShowInvalidInputMessage(val msg: String?) : PageEvent()
    }
}