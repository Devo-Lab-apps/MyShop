package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.EntryMetadata.RECURRING_ENTRY_FREQUENCY
import com.labs.devo.apps.myshop.data.models.notebook.EntryMetadata.RECURRING_ENTRY_TIME
import com.labs.devo.apps.myshop.databinding.AddEditEntryFragmentBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ENTRY
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE_ID
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddEditEntryFragment : Fragment(R.layout.add_edit_entry_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: AddEditEntryFragmentBinding

    private val viewModel: AddEditEntryViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var selectedRepeatingTime: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditEntryFragmentBinding.bind(view)

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            val items = listOf("Daily", "Weekly", "Monthly", "Biweekly")
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
            )
            (repeatingEntryFrequency).adapter = adapter

            recurringTime.setOnClickListener {
                pickTime()
            }

            addEditEntryBtn.setOnClickListener {
                addEditEntryBtn.isEnabled = false
                isRecurring.isEnabled = false
                if (viewModel.operation == ADD_ENTRY_OPERATION) {
                    addOperation()
                } else {
                    editOperation()
                }
            }

            if (viewModel.operation == EDIT_ENTRY_OPERATION) {
                addEditEntryBtn.text = getString(R.string.update_entry)
                viewModel.entry?.let { e ->
                    entryTitle.setText(e.entryTitle)
                    entryAmount.setText(e.entryAmount.toString())
                } ?: run {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_entry)))
                    findNavController().navigateUp()
                }
                isRecurring.visibility = View.GONE
            } else {
                isRecurring.setOnCheckedChangeListener { _, isChecked ->
                    recurringForm.isVisible = isChecked
                }
            }


        }
    }

    private fun pickTime() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Recurring time of day")
                .build()
        picker.addOnPositiveButtonClickListener {
            val selectedHour = picker.hour
            val selectedMinute = picker.minute
            val hour = if (selectedHour < 10) "0$selectedHour" else "$selectedHour"
            val minute = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
            selectedRepeatingTime = "$hour:$minute"
        }
        picker.show(requireActivity().supportFragmentManager, "Sample Tag")
    }

    private fun editOperation() {
        binding.apply {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
            viewModel.entry?.let { e ->
                val entryTitle = entryTitle.text.toString()
                val amountText = entryAmount.text.toString()
                try {
                    val amount = amountText.toDouble()
                    val newEntry = e.copy(
                        entryTitle = entryTitle,
                        entryAmount = amount,
                        modifiedAt = System.currentTimeMillis()
                    )
                    viewModel.updateEntry(e, newEntry)
                } catch (ex: NumberFormatException) {
                    dataStateHandler.onDataStateChange(
                        DataState.message<Nothing>(getString(R.string.enter_a_valid_number))
                    )
                    addEditEntryBtn.isEnabled = true
                    isRecurring.isEnabled = true
                }

            } ?: run {
                dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_entry)))
                findNavController().navigateUp()
            }
        }
    }

    private fun addOperation() {
        binding.apply {
            val entryTitle = entryTitle.text.toString()
            val amountText = entryAmount.text.toString()
            try {
                val amount = amountText.toDouble()
                val isRecurringEntry = isRecurring.isChecked
                if (isRecurringEntry) {
                    if (validateRecurringData()) {
                        val metadata = mutableMapOf(
                            RECURRING_ENTRY_TIME to selectedRepeatingTime,
                            RECURRING_ENTRY_FREQUENCY to repeatingEntryFrequency.selectedItem.toString()
                        )
                        val entry = Entry(
                            pageId = viewModel.pageId,
                            entryTitle = entryTitle,
                            entryAmount = amount,
                            isRepeating = true,
                            entryMetadata = metadata
                        )
                        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                        viewModel.addEntry(entry)
                    } else {
                        addEditEntryBtn.isEnabled = true
                        isRecurring.isEnabled = true
                    }
                } else {
                    val entry = Entry(
                        pageId = viewModel.pageId,
                        entryTitle = entryTitle,
                        entryAmount = amount
                    )
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    viewModel.addEntry(entry)
                }
            } catch (ex: NumberFormatException) {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(getString(R.string.enter_a_valid_number))
                )
                addEditEntryBtn.isEnabled = true
                isRecurring.isEnabled = true
            }
        }
    }

    private fun validateRecurringData(): Boolean {
        if (!this::selectedRepeatingTime.isInitialized) {
            dataStateHandler.onDataStateChange(DataState.message<Nothing>("Please select time first."))
            return false
        }
        return true
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvents(event)
            }
        }
    }

    private fun collectEvents(event: AddEditEntryViewModel.AddEditEntryEvent) {
        when (event) {
            is AddEditEntryViewModel.AddEditEntryEvent.EntryInserted -> {
                dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditEntryViewModel.AddEditEntryEvent.ShowInvalidInputMessage -> {
                if (event.msg != null) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                }
            }
            is AddEditEntryViewModel.AddEditEntryEvent.EntryUpdated -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditEntryViewModel.AddEditEntryEvent.EntryDeleted -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
        }
        binding.addEditEntryBtn.isEnabled = true
        binding.isRecurring.isEnabled = true
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