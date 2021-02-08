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
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditNotebookFragment : Fragment(R.layout.add_notebook_fragment) {

    private lateinit var binding: AddNotebookFragmentBinding

    private val viewModel: AddEditNotebookViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var operation: String

    private var notebook: Notebook? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddNotebookFragmentBinding.bind(view)

        arguments?.apply {
            notebook = getParcelable("notebook")
            operation = getString(NotebookFragment.NotebookConstants.OPERATION).toString()
        }
        initView()

        observeEvents()
    }


    private fun initView() {
        binding.apply {
            addEditNotebookBtn.setOnClickListener {
                addEditNotebookBtn.text = "Add Notebook"
                if (operation == NotebookFragment.NotebookConstants.ADD_NOTEBOOK_OPERATION) {
                    val notebookName = notebookName.text.toString()
                    val notebook = Notebook(
                        notebookName = notebookName
                    )
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    viewModel.addNotebook(notebook)
                } else {
                    addEditNotebookBtn.text = "Update Notebook"
                    notebookName.setText(notebook!!.notebookName)
                    val notebookName = notebookName.text.toString()
                    val newNotebook = notebook!!.copy(
                        notebookName = notebookName,
                        modifiedAt = System.currentTimeMillis()
                    )
                    viewModel.updateNotebook(notebook!!, newNotebook)
                }
            }
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
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
                        dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                        findNavController().navigateUp()
                    }
                    is AddEditNotebookViewModel.AddEditNotebookEvent.NotebookDeleted -> {
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