package com.labs.devo.apps.myshop.view.activity.notebook.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.FragmentPageBinding
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookFragmentDirections
import kotlinx.coroutines.flow.collect

class PageFragment : Fragment(R.layout.fragment_page) {


    private lateinit var binding: FragmentPageBinding

    private val viewModel: PageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPageBinding.bind(view)


        initView()


        observeEvents()

    }


    private fun initView() {
        binding.selectNotebookButton.setOnClickListener {
            viewModel.onNotebookSelect()
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is PageViewModel.PageEvent.NavigateToNotebookFragment -> {
                        showNotebookFragment()
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
}