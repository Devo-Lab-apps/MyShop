package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.databinding.FragmentNotebookBinding
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.ADD_NOTEBOOK_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.EDIT_NOTEBOOK_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.adapter.notebook.NotebookListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotebookFragment : DialogFragment(R.layout.fragment_notebook) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val viewModel: NotebookViewModel by viewModels()

    private lateinit var binding: FragmentNotebookBinding

    private lateinit var notebookAdapter: NotebookListAdapter

    private lateinit var dataStateHandler: DataStateListener


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
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        notebookAdapter = NotebookListAdapter(object : NotebookListAdapter.OnNotebookClick {
            override fun onClick(notebook: Notebook) {
                if (notebook.notebookId == "foreign" || notebook.notebookName == "Foreign") {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>("You can't edit foreign transactions."))
                    return
                }
                val args = bundleOf(
                    "notebook" to notebook,
                    OPERATION to EDIT_NOTEBOOK_OPERATION
                )
                findNavController().navigate(R.id.addEditNotebookFragment, args)
            }
        })

        notebookAdapter.submitList(listOf(Notebook("", "")))

        binding.apply {
            notebooksRecyclerView.apply {
                adapter = notebookAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            addEditNotebookBtn.setOnClickListener {
                val args = bundleOf(
                    OPERATION to ADD_NOTEBOOK_OPERATION
                )
                findNavController().navigate(R.id.addEditNotebookFragment, args)
            }

        }


    }

    /**
     * Observe events from view model.
     */
    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is NotebookViewModel.NotebookEvent.GetNotebooks -> {
                        val notebooks = event.notebooks
                        dataStateHandler.onDataStateChange(event.dataState)
                        notebookAdapter.submitList(notebooks.toMutableList())
                    }
                    is NotebookViewModel.NotebookEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        }
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

    object NotebookConstants {
        const val OPERATION = "operation"
        const val ADD_NOTEBOOK_OPERATION = "add_notebook"
        const val EDIT_NOTEBOOK_OPERATION = "edit_notebook"
        const val ADD_PAGE_OPERATION = "add_page"
        const val EDIT_PAGE_OPERATION = "edit_page"

    }

}