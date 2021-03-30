package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.FragmentMicroEntryBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.RECURRING_ENTRY_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MicroEntryFragment : Fragment(R.layout.fragment_micro_entry) {

    private lateinit var binding: FragmentMicroEntryBinding

    private val viewModel: MicroEntryViewModel by viewModels()

    private lateinit var recurringEntryId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMicroEntryBinding.bind(view)

        arguments.apply {
            recurringEntryId = this!!.getString(RECURRING_ENTRY_ID)!!
        }

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            microEntryRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())

            }

            addMicroEntry.setOnClickListener {
                addEntry()
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect {
                collectEvent(it)
            }
        }
    }

    private suspend fun collectEvent(it: MicroEntryViewModel.MicroEntryEvent) {

    }


    private fun addEntry() {

    }


}