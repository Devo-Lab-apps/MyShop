package com.labs.devo.apps.myshop.view.activity.notebook.entry

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.FragmentRecurringEntryBinding
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE_ID
import com.labs.devo.apps.myshop.view.adapter.notebook.RecurringEntryListAdapter
import com.labs.devo.apps.myshop.view.util.DataStateListener


class RecurringEntryFragment : Fragment(R.layout.fragment_recurring_entry) {

    private lateinit var binding: FragmentRecurringEntryBinding

    private val viewModel: RecurringEntryViewModel by viewModels()

    private lateinit var entryAdapter: RecurringEntryListAdapter

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var pageId: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecurringEntryBinding.bind(view)

        arguments?.apply {
            pageId = getString(PAGE_ID)!!
        }

        initView()
        observeEvents()
    }

    private fun initView() {

        entryAdapter = RecurringEntryListAdapter()
        binding.apply {
            recurringEntries.apply {
                setHasFixedSize(true)
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        entryAdapter.submitList(
            listOf(RecurringEntry("", "", "One"))
        )
    }

    private fun observeEvents() {

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