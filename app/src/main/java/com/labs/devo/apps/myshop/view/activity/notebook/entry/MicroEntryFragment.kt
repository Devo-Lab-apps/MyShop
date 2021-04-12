package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.mediator.GenericLoadStateAdapter
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.FragmentMicroEntryBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
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
import java.util.*

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
        viewModel.setRecurringEntry(recurringEntry)

        initView()
        observeEvents()
    }

    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.microEntryToolbar)
        setHasOptionsMenu(true)

        microEntryAdapter = MicroEntryAdapter(this, recurringEntry)

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
            viewModel.entries.collectLatest {
                microEntryAdapter.submitData(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            microEntryAdapter.loadStateFlow.collectLatest { state ->
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_micro_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_micro_entry -> {
                val builderRange = MaterialDatePicker.Builder.dateRangePicker()

//                builderRange.setCalendarConstraints(limitRange().build())
                val pickerRange = builderRange.build()
                pickerRange.show(requireActivity().supportFragmentManager, pickerRange.toString())
                pickerRange.addOnPositiveButtonClickListener {
                    viewModel.selectDate(it.first ?: 0, it.second ?: Long.MAX_VALUE)
                }
                true
            }
            R.id.action_sort_micro_entry_by_date -> {
                viewModel.setOrderBy(Entry::modifiedAt.name)
                true
            }
            R.id.action_sort_micro_entry_by_name -> {
                viewModel.setOrderBy(Entry::entryTitle.name)
                true
            }
            R.id.action_sync_micro_entries -> {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncMicroEntries()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    private fun limitRange(): CalendarConstraints.Builder {

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarStart: Calendar = Calendar.getInstance()
        val calendarEnd: Calendar = Calendar.getInstance()

        val year = 2021
        val startMonth = 1
        val startDate = 1

        val endMonth = 11
        val endDate = 28

        calendarStart.set(year, startMonth - 1, startDate - 1)
        calendarEnd.set(year, endMonth - 1, endDate)

        val minDate = calendarStart.timeInMillis
        val maxDate = calendarEnd.timeInMillis

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))

        return constraintsBuilderRange
    }

    class RangeValidator(private val minDate: Long, private val maxDate: Long) :
        CalendarConstraints.DateValidator {


        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong()
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented")
        }

        override fun describeContents(): Int {
            TODO("not implemented")
        }

        override fun isValid(date: Long): Boolean {
            return !(minDate > date || maxDate < date)

        }

        companion object CREATOR : Parcelable.Creator<RangeValidator> {
            override fun createFromParcel(parcel: Parcel): RangeValidator {
                return RangeValidator(parcel)
            }

            override fun newArray(size: Int): Array<RangeValidator?> {
                return arrayOfNulls(size)
            }
        }

    }
}