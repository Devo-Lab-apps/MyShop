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
import com.labs.devo.apps.myshop.data.mediator.GenericLoadStateAdapter
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.FragmentMicroEntryBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ENTRY
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY
import com.labs.devo.apps.myshop.view.adapter.notebook.MicroEntryAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MicroEntryFragment : Fragment(R.layout.fragment_micro_entry),
    MicroEntryAdapter.OnMicroEntryClick {

    private lateinit var binding: FragmentMicroEntryBinding

    private val viewModel: MicroEntryViewModel by viewModels()

    private lateinit var recurringEntry: RecurringEntry

    private lateinit var microEntryAdapter: MicroEntryAdapter

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMicroEntryBinding.bind(view)

        arguments.apply {
            recurringEntry = this!!.getParcelable(RECURRING_ENTRY)!!
        }

        initView()
        observeEvents()
    }

    private fun initView() {
        microEntryAdapter = MicroEntryAdapter(this)

        binding.apply {
            microEntryRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter =
                    microEntryAdapter.withLoadStateFooter(
                        GenericLoadStateAdapter(microEntryAdapter::retry)
                    )
                itemAnimator?.changeDuration = 0
            }

            addMicroEntry.setOnClickListener {
                addEntry()
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect {
                collectEvent(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getMicroEntries(recurringEntry).collectLatest {
                microEntryAdapter.submitData(it)
            }
        }
    }

    private fun collectEvent(event: MicroEntryViewModel.MicroEntryEvent) {
        when (event) {
            MicroEntryViewModel.MicroEntryEvent.AddMicroEntryEvent -> {
                val args = bundleOf(
                    RECURRING_ENTRY to recurringEntry,
                    OPERATION to ADD_ENTRY_OPERATION
                )
                findNavController().navigate(R.id.addEditMicroEntryFragment, args)
            }
            is MicroEntryViewModel.MicroEntryEvent.EditMicroEntryEvent -> {
                val args = bundleOf(
                    RECURRING_ENTRY to recurringEntry,
                    ENTRY to event.entry,
                    OPERATION to EDIT_ENTRY_OPERATION
                )
                findNavController().navigate(R.id.addEditMicroEntryFragment, args)
            }
            is MicroEntryViewModel.MicroEntryEvent.ShowInvalidInputMessage -> {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        event.msg ?: getString(R.string.unknown_error_occurred)
                    )
                )
            }
        }
    }


    private fun addEntry() {
        viewModel.addMicroEntry()
    }

    override fun onClick(entry: Entry) {
        viewModel.onEntryClick(entry)
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