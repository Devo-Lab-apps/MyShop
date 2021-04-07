package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.AddEditMicroEntryFragmentBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_ENTRY_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ENTRY
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditMicroEntryFragment : Fragment(R.layout.add_edit_micro_entry_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: AddEditMicroEntryFragmentBinding

    private val viewModel: AddEditMicroEntryViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var operation: String

    private lateinit var repeatingEntry: RecurringEntry

    private var entry: Entry? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditMicroEntryFragmentBinding.bind(view)

        arguments?.apply {
            repeatingEntry = getParcelable(NotebookActivity.NotebookConstants.RECURRING_ENTRY)!!
            entry = getParcelable(ENTRY)
            operation = getString(NotebookActivity.NotebookConstants.OPERATION).toString()
        }

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addEditMicroEntryBtn.setOnClickListener {
                addEditMicroEntryBtn.isEnabled = false
                if (operation == NotebookActivity.NotebookConstants.ADD_ENTRY_OPERATION) {
                    addOperation()
                } else if (operation == EDIT_ENTRY_OPERATION) {
                    editOperation()
                }
            }

            if (operation == EDIT_ENTRY_OPERATION) {
                addEditMicroEntryBtn.text = getString(R.string.update_micro_entry)
                microEntryAmount.setText(entry?.entryAmount?.toString() ?: "0.0")
                deleteMicroEntryBtn.visibility = View.VISIBLE
                deleteMicroEntryBtn.setOnClickListener {
                    deleteOperation()
                }
            } else if (operation == ADD_ENTRY_OPERATION){
                addEditMicroEntryBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun deleteOperation() {
        binding.apply {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
            viewModel.deleteEntry(repeatingEntry, entry!!)
        }
    }

    private fun editOperation() {
        binding.apply {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
            val amountText = microEntryAmount.text.toString()
            try {
                val amount = amountText.toDouble()
                val newEntry = entry!!.copy(
                    entryAmount = amount,
                    modifiedAt = System.currentTimeMillis()
                )
                viewModel.updateEntry(repeatingEntry, entry!!, newEntry)
            } catch (ex: NumberFormatException) {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(getString(R.string.enter_a_valid_number))
                )
                addEditMicroEntryBtn.isEnabled = true
            }

        }
    }


    private fun addOperation() {
        binding.apply {
            val amountText = microEntryAmount.text.toString()
            try {
                val amount = amountText.toDouble()
                val entry = Entry(
                    pageId = repeatingEntry.pageId,
                    entryTitle = repeatingEntry.name,
                    entryDescription = repeatingEntry.description,
                    entryAmount = amount
                )
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.addMicroEntry(repeatingEntry, entry)
            } catch (ex: NumberFormatException) {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(getString(R.string.enter_a_valid_number))
                )
                addEditMicroEntryBtn.isEnabled = true
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvents(event)
            }
        }
    }

    private fun collectEvents(event: AddEditMicroEntryViewModel.AddEditMicroEntryEvent) {
        when (event) {
            is AddEditMicroEntryViewModel.AddEditMicroEntryEvent.MicroEntryInserted -> {
                dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditMicroEntryViewModel.AddEditMicroEntryEvent.ShowInvalidInputMessage -> {
                if (event.msg != null) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                }
            }
            is AddEditMicroEntryViewModel.AddEditMicroEntryEvent.MicroEntryUpdated -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditMicroEntryViewModel.AddEditMicroEntryEvent.MicroEntryDeleted -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
        }
        binding.addEditMicroEntryBtn.isEnabled = true
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