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
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.AddEditEntryFragmentBinding
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment
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

    private lateinit var page: Page

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditEntryFragmentBinding.bind(view)
        arguments?.apply {
            entry = getParcelable("entry")
            page = getParcelable("page")!!
            operation = getString(NotebookFragment.NotebookConstants.OPERATION).toString()
        }

        initView()

        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addEditEntryBtn.setOnClickListener {
                if (operation == NotebookFragment.NotebookConstants.ADD_ENTRY_OPERATION) {
                    val entryTitle = entryTitle.text.toString()
                    val amount = entryAmount.text.toString()
                    val entry = Entry(
                        pageId = page.pageId,
                        entryTitle = entryTitle,
                        entryAmount = amount.toDouble()
                    )
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    viewModel.addEntry(entry)
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    val entryTitle = entryTitle.text.toString()
                    val entryAmount = entryAmount.text.toString().toDouble()
                    val newEntry = entry!!.copy(
                        entryTitle = entryTitle,
                        entryAmount = entryAmount,
                        modifiedAt = System.currentTimeMillis()
                    )
                    viewModel.updateEntry(entry!!, newEntry)
                }
            }

            if (operation == NotebookFragment.NotebookConstants.EDIT_ENTRY_OPERATION) {
                addEditEntryBtn.text = "Update Entry"
                entry?.let { e ->
                    entryTitle.setText(e.entryTitle)
                    entryAmount.setText(e.entryAmount.toString())
                }
            }
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
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