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
import com.labs.devo.apps.myshop.databinding.NotebookSettingsFragmentBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotebookSettingsFragment : Fragment(R.layout.notebook_settings_fragment) {

    private lateinit var binding: NotebookSettingsFragmentBinding

    private val viewModel: NotebookSettingsViewModel by viewModels()

    private lateinit var notebook: Notebook

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NotebookSettingsFragmentBinding.bind(view)
        init()

        observeEvents()
    }

    private fun init() {

        arguments?.apply {
            notebook = getParcelable("notebook")!!
        }

        binding.apply {
            notebooksName.setText(notebook.notebookName)

            updateNotebookBtn.setOnClickListener {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                val notebookName = notebooksName.text.toString()
                val newNotebook = notebook.copy(
                    notebookName = notebookName,
                    modifiedAt = System.currentTimeMillis()
                )
                viewModel.updateNotebook(notebook, newNotebook)
            }

            deleteNotebookBtn.setOnClickListener {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.deleteNotebook(notebook.notebookId)
            }
        }


    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is NotebookSettingsViewModel.NotebookSettingsEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                        } else {
                            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                        }
                    }
                    is NotebookSettingsViewModel.NotebookSettingsEvent.NotebookUpdated -> {
                        dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                        findNavController().navigateUp()
                    }
                    is NotebookSettingsViewModel.NotebookSettingsEvent.NotebookDeleted -> {
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