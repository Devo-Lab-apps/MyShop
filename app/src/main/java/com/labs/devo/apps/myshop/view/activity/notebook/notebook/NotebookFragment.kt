package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.NotebookMetadataConstants
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.databinding.FragmentNotebookBinding
import com.labs.devo.apps.myshop.util.PreferencesManager
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.NOTEBOOK
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.adapter.notebook.NotebookListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotebookFragment : DialogFragment(R.layout.fragment_notebook),
    NotebookListAdapter.OnNotebookClick, NotebookListAdapter.OnNotebookSettingsClick {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val viewModel: NotebookViewModel by viewModels()

    private lateinit var binding: FragmentNotebookBinding

    private lateinit var notebookAdapter: NotebookListAdapter

    private lateinit var dataStateHandler: DataStateListener

    @Inject
    lateinit var preferencesManager: PreferencesManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotebookBinding.bind(view)
        initView()
        viewModel.getNotebooks()
        observeEvents()
    }

    /**
     * Init the view to be displayed.
     */
    private fun initView() {
        (activity as NotebookActivity).setSupportActionBar(binding.notebookToolbar)
        setHasOptionsMenu(true)
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        notebookAdapter = NotebookListAdapter(this, this)

        notebookAdapter.submitList(mutableListOf())

        binding.apply {
            notebooksRecyclerView.apply {
                adapter = notebookAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            addEditNotebookBtn.setOnClickListener {
                viewModel.addNotebook()
            }

        }


    }

    /**
     * Observe events from view model.
     */
    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvent(event)
            }
        }
    }

    private suspend fun collectEvent(event: NotebookViewModel.NotebookEvent) {
        when (event) {
            is NotebookViewModel.NotebookEvent.GetNotebooks -> {
                val notebooks = event.notebooks
                dataStateHandler.onDataStateChange(event.dataState)
                val foreignNotebook =
                    notebooks.firstOrNull { notebook -> notebook.notebookId == FirebaseConstants.foreignNotebookKey }
                if (foreignNotebook != null) {
                    foreignNotebook.metadata[NotebookMetadataConstants.importStatus]?.let { s ->
                        val status = s.toInt()
                        preferencesManager.setForeignImported(status)
                    }
                } else {
                    //TODO handle it some way
//                            dataStateHandler.onDataStateChange(DataState.message<Nothing>("Some error occurred."))
//                            // clog("foreign can't be null).
//                            return@collect
                }
                notebookAdapter.submitList(notebooks.toMutableList())
            }
            is NotebookViewModel.NotebookEvent.ShowInvalidInputMessage -> {
                if (event.msg != null) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                }
            }
            is NotebookViewModel.NotebookEvent.AddNotebookEvent -> {
                val args = bundleOf(
                    OPERATION to NotebookActivity.NotebookConstants.ADD_NOTEBOOK_OPERATION
                )
                findNavController().navigate(R.id.addEditNotebookFragment, args)
            }
            is NotebookViewModel.NotebookEvent.EditNotebookEvent -> {
                val notebook = event.notebook
                if (notebook.notebookId == FirebaseConstants.foreignNotebookKey || notebook.notebookName == FirebaseConstants.foreignNotebookName) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(getString(R.string.cant_update_foreign_notebook)))
                    return
                }
                val args = bundleOf(
                    NOTEBOOK to notebook,
                    OPERATION to NotebookActivity.NotebookConstants.EDIT_NOTEBOOK_OPERATION
                )
                findNavController().navigate(R.id.addEditNotebookFragment, args)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_noteboook, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sync_notebook -> {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncNotebooks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSettingsClick(notebook: Notebook) {
        viewModel.editNotebook(notebook)
    }

    override fun onClick(notebook: Notebook) {
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        viewLifecycleOwner.lifecycleScope.launch {
            preferencesManager.updateCurrentSelectedNotebook(
                Pair(
                    notebook.notebookId,
                    notebook.notebookName
                )
            )
            findNavController().navigateUp()
        }
    }

}