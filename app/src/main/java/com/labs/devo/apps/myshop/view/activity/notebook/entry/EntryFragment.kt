package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.databinding.FragmentEntryBinding
import com.labs.devo.apps.myshop.view.adapter.entry.EntryListAdapter
import kotlinx.coroutines.flow.collect

class EntryFragment : Fragment(R.layout.fragment_entry) {


    private val viewModel: EntryViewModel by viewModels()

    private lateinit var binding: FragmentEntryBinding

    private lateinit var entryListAdapter: EntryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEntryBinding.bind(view)


        initView()
        observeEvents()

    }


    private fun initView() {
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

            }
        }
    }
}