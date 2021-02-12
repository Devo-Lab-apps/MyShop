package com.labs.devo.apps.myshop.view.activity.notebook.page

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.FragmentPageBinding
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.ADD_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.EDIT_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragment.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.adapter.Page.PageListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PageFragment : Fragment(R.layout.fragment_page) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: FragmentPageBinding

    private val viewModel: PageViewModel by viewModels()

    private lateinit var pageListAdapter: PageListAdapter

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPageBinding.bind(view)

        initView()

        viewModel.getPages("L4EnH4u6gfJ1JopyQ9rp")
        observeEvents()

    }


    private fun initView() {
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        pageListAdapter = PageListAdapter(object : PageListAdapter.OnPageClick {
            override fun onClick(page: Page) {
                val args = bundleOf(
                    OPERATION to EDIT_PAGE_OPERATION,
                    "page" to page
                )
                findNavController().navigate(R.id.addEditPageFragment, args)
            }
        })

        pageListAdapter.submitList(mutableListOf())

        binding.apply {
            selectNotebookButton.setOnClickListener {
                viewModel.onNotebookSelect()
            }

            pageRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pageListAdapter
            }

            addPageBtn.setOnClickListener {
                val args = bundleOf(
                    OPERATION to ADD_PAGE_OPERATION
                )
                findNavController().navigate(R.id.addEditPageFragment, args)
            }

            syncPages.setOnClickListener {
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.syncPages("L4EnH4u6gfJ1JopyQ9rp")
            }

        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is PageViewModel.PageEvent.NavigateToNotebookFragment -> {
                        showNotebookFragment()
                    }
                    is PageViewModel.PageEvent.GetPagesEvent -> {
                        dataStateHandler.onDataStateChange(event.dataState)
                        val pages = event.pages
                        pageListAdapter.submitList(pages)
                    }
                    is PageViewModel.PageEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        }
                    }
                }
            }
        }
    }

    private fun showNotebookFragment() {
        val options = NavOptions.Builder()
        options.setEnterAnim(R.anim.open_notebook_fragment)
        options.setExitAnim(R.anim.exit_notebook_fragment)
        options.setPopEnterAnim(R.anim.pop_enter_notebook_fragment)
        options.setPopExitAnim(R.anim.pop_exit_notebook_fragment)
        findNavController().navigate(R.id.notebookFragment, null, options.build())
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