package com.labs.devo.apps.myshop.view.activity.notebook.entry

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.FragmentEntryBinding
import com.labs.devo.apps.myshop.util.extensions.onQueryTextChanged
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment
import com.labs.devo.apps.myshop.view.adapter.entry.EntryListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val viewModel: EntryViewModel by viewModels()

    private lateinit var binding: FragmentEntryBinding

    private lateinit var entryListAdapter: EntryListAdapter

    private lateinit var page: Page

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEntryBinding.bind(view)

        arguments?.apply {
            page = getParcelable("page")!!
        }

        initView()
        observeEvents()
        viewModel.getEntries(page.pageId, "")
    }


    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.entryToolbar)
        setHasOptionsMenu(true)
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        entryListAdapter = EntryListAdapter(object : EntryListAdapter.OnEntryClick {
            override fun onClick(entry: Entry) {
                val args = bundleOf(
                    NotebookFragment.NotebookConstants.OPERATION to NotebookFragment.NotebookConstants.EDIT_ENTRY_OPERATION,
                    "page" to page,
                    "entry" to entry
                )
                findNavController().navigate(R.id.addEditEntryFragment, args)
            }
        })

        entryListAdapter.submitList(mutableListOf())
        binding.apply {
            entryRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = entryListAdapter
            }
            addEntry.setOnClickListener {
                val args = bundleOf(
                    NotebookFragment.NotebookConstants.OPERATION to NotebookFragment.NotebookConstants.ADD_ENTRY_OPERATION,
                    "page" to page
                )
                findNavController().navigate(R.id.addEditEntryFragment, args)
            }
            entryToolbar.title = page.pageName
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is EntryViewModel.EntryEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        }
                    }
                    is EntryViewModel.EntryEvent.GetEntries -> {
                        printLogD(TAG, event.entries)
                        dataStateHandler.onDataStateChange(event.dataState)
                        entryListAdapter.submitList(event.entries.toMutableList())
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_entry_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search_entry)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.getEntries(page.pageId, it)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_entry_by_date -> {
                true
            }
            R.id.action_sort_entry_by_name -> {
                true
            }
            R.id.action_sync_entries -> {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncEntries(page.pageId)
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
}