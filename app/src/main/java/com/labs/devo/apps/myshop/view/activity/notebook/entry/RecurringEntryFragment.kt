package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.FragmentRecurringEntryBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE_ID
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY_ID
import com.labs.devo.apps.myshop.view.adapter.notebook.RecurringEntryListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecurringEntryFragment : Fragment(R.layout.fragment_recurring_entry),
    RecurringEntryListAdapter.OnRecurringEntryClick {

    private lateinit var binding: FragmentRecurringEntryBinding

    private val viewModel: RecurringEntryViewModel by viewModels()

    private lateinit var entryAdapter: RecurringEntryListAdapter

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var pageId: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecurringEntryBinding.bind(view)

        arguments?.apply {
            pageId = getString(PAGE_ID)!!
        }

        initView()
        observeEvents()
        viewModel.getRecurringEntries(pageId)
    }

    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.recurringEntryToolbar)
        setHasOptionsMenu(true)
        entryAdapter = RecurringEntryListAdapter(this)
        binding.apply {
            recurringEntries.apply {
                setHasFixedSize(true)
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun observeEvents() {
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvent(event)
            }
        }
    }

    private fun collectEvent(event: RecurringEntryViewModel.RecurringEntryEvent) {
        when (event) {
            is RecurringEntryViewModel.RecurringEntryEvent.GetRecurringEntriesEvent -> {
                entryAdapter.submitList(event.entries)
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
            }
            is RecurringEntryViewModel.RecurringEntryEvent.ShowInvalidInputMessage -> {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        event.msg ?: getString(R.string.unknown_error_occurred)
                    )
                )
            }
            is RecurringEntryViewModel.RecurringEntryEvent.NavigateToMicroEntryFragment -> {
                val args = bundleOf(
                    RECURRING_ENTRY to event.entry
                )
                findNavController().navigate(R.id.microEntryFragment, args)
            }
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

    override fun onClick(recurringEntry: RecurringEntry) {
        viewModel.onRecurringEntryClick(recurringEntry)
    }
}