package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.databinding.FragmentNotebookBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.Companion.NO_NOTEBOOK_MSG
import com.labs.devo.apps.myshop.view.adapter.notebook.NotebookListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotebookFragment : DialogFragment(R.layout.fragment_notebook) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val viewModel: NotebookViewModel by viewModels()

    private lateinit var binding: FragmentNotebookBinding

    private lateinit var notebookAdapter: NotebookListAdapter

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var job: Job


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotebookBinding.bind(view)
        initView()
        job = viewModel.getNotebooks()
        observeEvents()
    }

    /**
     * Init the view to be displayed.
     */
    private fun initView() {
        notebookAdapter = NotebookListAdapter(object : NotebookListAdapter.OnNotebookClick {
            override fun onClick(notebook: Notebook) {
                Toast.makeText(requireContext(), notebook.notebookId, Toast.LENGTH_LONG).show()
            }
        })

        notebookAdapter.submitList(listOf(Notebook("", "")))

        binding.apply {
            notebooksRecyclerView.apply {
                adapter = notebookAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
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
                        if (notebooks.isEmpty()) {
                            binding.notebooksStatus.text = NO_NOTEBOOK_MSG
                            binding.notebooksStatus.visibility = View.VISIBLE
                        } else {
                            binding.notebooksStatus.visibility = View.GONE
                        }
                        notebookAdapter.submitList(notebooks.toMutableList())
                    }
                    is NotebookViewModel.NotebookEvent.ShowInvalidInputMessage -> {
                        dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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