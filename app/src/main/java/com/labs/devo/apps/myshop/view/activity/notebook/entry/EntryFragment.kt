package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.mediator.GenericLoadStateAdapter
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.databinding.FragmentEntryBinding
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.util.extensions.onQueryTextChanged
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.adapter.entry.EntryListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry), EntryListAdapter.OnEntryClick {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val viewModel: EntryViewModel by viewModels()

    private lateinit var searchView: SearchView

    private lateinit var binding: FragmentEntryBinding

    private lateinit var entryListAdapter: EntryListAdapter

    private lateinit var dataStateHandler: DataStateListener

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEntryBinding.bind(view)

        initView()
        observeEvents()

    }


    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.entryToolbar)
        setHasOptionsMenu(true)

        //initial loading
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        entryListAdapter = EntryListAdapter(this)

        binding.apply {
            entryRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = entryListAdapter.withLoadStateFooter(
                    GenericLoadStateAdapter(entryListAdapter::retry)
                )
                itemAnimator?.changeDuration = 0
            }

            addEntry.setOnClickListener {
                viewModel.addEntry()
            }
            entryToolbar.title = viewModel.pageName
            entryToolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.entries.collectLatest { data ->
                binding.entryRecyclerView.scrollToPosition(0)
                entryListAdapter.submitData(data)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            entryListAdapter.loadStateFlow.collectLatest { state ->
                when (state.refresh) {
                    is LoadState.Error -> {
                        val error = (state.refresh as LoadState.Error).error
                        dataStateHandler.onDataStateChange(
                            DataState.message<Nothing>(
                                error.message ?: getString(R.string.unknown_error_occurred)
                            )
                        )
                    }
                    is LoadState.Loading -> {
                        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    }
                    else -> {
                        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                    }
                }
            }
        }
        viewModel.amountSum.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.totalSum.text = "Total: 0"
            } else {
                binding.totalSum.text = "Total: $it"
            }
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvent(event)
            }
        }
    }

    private fun collectEvent(event: EntryViewModel.EntryEvent) {
        when (event) {
            is EntryViewModel.EntryEvent.ShowInvalidInputMessage -> {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        event.msg ?: getString(R.string.unknown_error_occurred)
                    )
                )
            }
            is EntryViewModel.EntryEvent.AddEntryEvent -> {
                val action = EntryFragmentDirections.actionEntryFragmentToAddEditEntryFragment(
                    ADD_ENTRY_OPERATION, viewModel._pageId.value!!, null
                )
                findNavController().navigate(action)
            }
            is EntryViewModel.EntryEvent.EditEntryEvent -> {
                val action = EntryFragmentDirections.actionEntryFragmentToAddEditEntryFragment(
                    EDIT_ENTRY_OPERATION, viewModel._pageId.value!!, event.entry
                )
                findNavController().navigate(action)
            }
            is EntryViewModel.EntryEvent.NavigateToRecurringEntryEvent -> {
                val action =
                    EntryFragmentDirections.actionEntryFragmentToRecurringEntryFragment(viewModel._pageId.value!!)
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_entry_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search_entry)
        searchView = searchItem.actionView as SearchView

        val query = viewModel.searchQuery.value
        if (query != null && query.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(query, false)
        }
        searchView.onQueryTextChanged {
            viewModel.setSearchQuery(it)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_entry_by_date -> {
                viewModel.setOrderBy(Entry::modifiedAt.name)
                true
            }
            R.id.action_sort_entry_by_name -> {
                viewModel.setOrderBy(Entry::entryTitle.name)
                true
            }
            R.id.action_sync_entries -> {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncEntries()
                true
            }
            R.id.open_recurring_entry -> {
                viewModel.onRecurringEntry()
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

    override fun onClick(entry: Entry) {
        viewModel.onEntryClick(entry)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}