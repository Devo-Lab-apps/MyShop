package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.FragmentEntryBinding
import com.labs.devo.apps.myshop.view.adapter.entry.EntryListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry) {


    private val viewModel: EntryViewModel by viewModels()

    private lateinit var binding: FragmentEntryBinding

    private lateinit var entryListAdapter: EntryListAdapter

    private lateinit var page: Page

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEntryBinding.bind(view)

        arguments?.apply {
            page = getParcelable("page")!!
        }

        initView()
        observeEvents()
        viewModel.getEntries(page.pageId)
    }


    private fun initView() {
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
        entryListAdapter = EntryListAdapter(object : EntryListAdapter.OnEntryClick {
            override fun onClick(entry: Entry) {
                TODO("Not yet implemented")
            }
        })

        binding.apply {
            entryRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = entryListAdapter
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is EntryViewModel.EntryEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        }
                    }
                    is EntryViewModel.EntryEvent.GetEntries -> {
                        dataStateHandler.onDataStateChange(event.dataState)
                        entryListAdapter.submitList(event.entries)
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