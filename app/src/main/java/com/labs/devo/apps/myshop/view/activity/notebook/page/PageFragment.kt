package com.labs.devo.apps.myshop.view.activity.notebook.page

import android.content.Context
import android.os.Bundle
import android.view.View
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
import com.labs.devo.apps.myshop.util.printLogD
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

        viewModel.getPages("ffsTqlNLKlBysFU0MiqI")
        observeEvents()

    }


    private fun initView() {

        pageListAdapter = PageListAdapter(object : PageListAdapter.OnPageClick {
            override fun onClick(page: Page) {
                printLogD(TAG, page)
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
                        val pages = event.pages
                        if (pages.isEmpty()) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>("No notebook for the selected notebook"))
                        }
                        printLogD(TAG, pages)
                        pageListAdapter.submitList(event.pages)
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