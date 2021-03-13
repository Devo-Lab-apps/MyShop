package com.labs.devo.apps.myshop.view.activity.notebook.page

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.ImportStatus
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.FragmentPageBinding
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.util.extensions.onQueryTextChanged
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.ADD_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.EDIT_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.NOTEBOOK_ID
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.adapter.notebook.PageListAdapter
import com.labs.devo.apps.myshop.view.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PageFragment : Fragment(R.layout.fragment_page), PageListAdapter.OnPageClick,
    PageListAdapter.OnPageSettingsClick {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: FragmentPageBinding

    private val viewModel: PageViewModel by viewModels()

    private lateinit var pageListAdapter: PageListAdapter

    private lateinit var dataStateHandler: DataStateListener

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private lateinit var notebookId: String

    private lateinit var queryParams: QueryParams

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPageBinding.bind(view)

        initView()
        observeEvents()

    }


    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.pageToolbar)
        setHasOptionsMenu(true)
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        binding.selectNotebookButton.isEnabled = false
        pageListAdapter = PageListAdapter(this, this)

        pageListAdapter.submitList(mutableListOf())

        binding.apply {
            selectNotebookButton.setOnClickListener {
                viewModel.onNotebookSelect()
            }

            pageRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pageListAdapter
            }

            addPageBtn.setOnClickListener {
                val args = bundleOf(
                    OPERATION to ADD_PAGE_OPERATION,
                    NOTEBOOK_ID to notebookId
                )
                findNavController().navigate(R.id.addEditPageFragment, args)
            }
        }
    }


    private fun observeEvents() {
        lifecycleScope.launch {
            combine(
                preferencesManager.currentSelectedNotebook,
                preferencesManager.pageQueryParams,
                preferencesManager.importStatus
            ) { notebook, qp, isForeignImported ->
                Triple(notebook, qp, isForeignImported)
            }.collect { (notebook, qp, isForeignImported) ->
                notebookId = notebook.first
                queryParams = qp
                binding.selectNotebookButton.text = notebook.second
                if (notebookId != FirebaseConstants.foreignNotebookKey || isForeignImported == ImportStatus.IMPORTED.ordinal) {
                    viewModel.getPages(notebookId, queryParams)
                } else {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>("Pages for foreign notebook are being imported. Please try to sync notebooks."))
                }
                binding.selectNotebookButton.isEnabled = true
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is PageViewModel.PageEvent.NavigateToNotebookFragment -> {
                        showNotebookFragment()
                    }
                    is PageViewModel.PageEvent.GetPagesEvent -> {
                        dataStateHandler.onDataStateChange(event.dataState)
                        val pages = event.pages
                        pageListAdapter.submitList(pages)
                    }
                    is PageViewModel.PageEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        }
                    }
                }
            }
        }
    }

    private fun showNotebookFragment() {
        val options = NavOptions.Builder()
        options.setEnterAnim(R.anim.open_notebook_fragment)
        options.setExitAnim(R.anim.exit_notebook_fragment)
        options.setPopEnterAnim(R.anim.pop_enter_notebook_fragment)
        options.setPopExitAnim(R.anim.pop_exit_notebook_fragment)
        findNavController().navigate(R.id.notebookFragment, null, options.build())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_page_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search_page)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            val whereQuery = queryParams.whereQuery
            val colName = Page::pageName.name
            whereQuery[colName] = WhereClause("LIKE", "%$it%", QUERY_TYPE.STRING)
            viewModel.getPages(notebookId, queryParams)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_page_by_date -> {
                QueryHelper.handleOrderBy(Page::modifiedAt.name, queryParams)
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesManager.updatePageQueryParams(queryParams)
                }
                true
            }
            R.id.action_sort_page_by_name -> {
                QueryHelper.handleOrderBy(Page::pageName.name, queryParams)
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesManager.updatePageQueryParams(queryParams)
                }
                true
            }
            R.id.action_sync_pages -> {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncPages(notebookId)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }

    }

    override fun onPageSettingsClick(page: Page) {
        val args = bundleOf(
            OPERATION to EDIT_PAGE_OPERATION,
            "page" to page
        )
        findNavController().navigate(R.id.addEditPageFragment, args)
    }


    override fun onClick(page: Page) {
        val args = bundleOf(
            "page" to page
        )
        findNavController().navigate(R.id.entryFragment, args)
    }
}