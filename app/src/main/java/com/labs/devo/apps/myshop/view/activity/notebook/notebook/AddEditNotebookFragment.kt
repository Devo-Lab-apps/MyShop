package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.databinding.AddNotebookFragmentBinding
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_NOTEBOOK_OPERATION
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNotebookFragment : Fragment(R.layout.add_notebook_fragment) {

    private lateinit var binding: AddNotebookFragmentBinding

    private val viewModel: AddEditNotebookViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var selectedNotebookId: String

    private lateinit var typedNotebookName: String

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddNotebookFragmentBinding.bind(view)

        initView()
        observeEvents()
    }


    private fun initView() {
        binding.apply {
            addEditNotebookBtn.setOnClickListener {
                if (viewModel.operation == NotebookActivity.NotebookConstants.ADD_NOTEBOOK_OPERATION) {
                    typedNotebookName = notebookName.text.toString()
                    val notebook = Notebook(
                        notebookName = typedNotebookName
                    )
                    addEditNotebookBtn.isEnabled = false
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    viewModel.addNotebook(notebook)
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    typedNotebookName = notebookName.text.toString()
                    viewModel.notebook?.let { n ->
                        val newNotebook = n.copy(
                            notebookName = typedNotebookName,
                            modifiedAt = System.currentTimeMillis()
                        )
                        addEditNotebookBtn.isEnabled = false
                        viewModel.updateNotebook(n, newNotebook)
                    } ?: run {
                        dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_notebook)))
                        findNavController().navigateUp()
                    }

                }

            }
            if (viewModel.operation == EDIT_NOTEBOOK_OPERATION) {
                addEditNotebookBtn.text = getString(R.string.update_notebook)
                viewModel.notebook?.let { n ->
                    notebookName.setText(n.notebookName)
                } ?: run {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.retry_updating_notebook)))
                    findNavController().navigateUp()
                }
            }
            typedNotebookName = notebookName.text.toString()
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            preferencesManager.currentSelectedNotebook.collect { (notebookId, _) ->
                selectedNotebookId = notebookId
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvent(event)
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


    private suspend fun collectEvent(event: AddEditNotebookViewModel.AddEditNotebookEvent) {
        when (event) {
            is AddEditNotebookViewModel.AddEditNotebookEvent.NotebookInserted -> {
                dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditNotebookViewModel.AddEditNotebookEvent.ShowInvalidInputMessage -> {
                if (event.msg != null) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                }
            }
            is AddEditNotebookViewModel.AddEditNotebookEvent.NotebookUpdated -> {
                if (selectedNotebookId == viewModel.notebook?.notebookId) {
                    preferencesManager.updateCurrentSelectedNotebook(
                        Pair(
                            viewModel.notebook!!.notebookId,
                            typedNotebookName
                        )
                    )
                    findNavController().navigateUp()
                }
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditNotebookViewModel.AddEditNotebookEvent.NotebookDeleted -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
        }
        binding.addEditNotebookBtn.isEnabled = true
    }
}
