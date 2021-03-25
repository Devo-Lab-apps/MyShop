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

    private lateinit var operation: String

    private var entry: Entry? = null

    private lateinit var pageId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditEntryFragmentBinding.bind(view)

        arguments?.apply {
            entry = getParcelable(ENTRY)
            pageId = getString(PAGE_ID)!!
            operation = getString(OPERATION).toString()
        }

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addEditEntryBtn.setOnClickListener {
                if (operation == ADD_ENTRY_OPERATION) {
                    val entryTitle = entryTitle.text.toString()
                    val amountText = entryAmount.text.toString()
                    try {
                        val amount = amountText.toDouble()
                        val entry = Entry(
                            pageId = pageId,
                            entryTitle = entryTitle,
                            entryAmount = amount
                        )
                        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                        viewModel.addEntry(entry)
                    } catch (ex: NumberFormatException) {
                        dataStateHandler.onDataStateChange(
                            DataState.message<Nothing>(getString(R.string.enter_a_valid_number))
                        )
                    }
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    entry?.let { e ->
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
                        }

                    } ?: run {
                        dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_entry)))
                        findNavController().navigateUp()
                    }
                }
            }

            if (operation == EDIT_ENTRY_OPERATION) {
                addEditEntryBtn.text = getString(R.string.update_entry)
                entry?.let { e ->
                    entryTitle.setText(e.entryTitle)
                    entryAmount.setText(e.entryAmount.toString())
                } ?: run {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_entry)))
                    findNavController().navigateUp()
                }
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