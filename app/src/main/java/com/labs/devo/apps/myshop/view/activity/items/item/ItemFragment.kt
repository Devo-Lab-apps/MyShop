package com.labs.devo.apps.myshop.view.activity.items.item

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED
import com.labs.devo.apps.myshop.data.mediator.GenericLoadStateAdapter
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.databinding.FragmentItemBinding
import com.labs.devo.apps.myshop.util.extensions.onQueryTextChanged
import com.labs.devo.apps.myshop.view.adapter.item.ItemListAdapter
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ItemFragment : Fragment(R.layout.fragment_item), ItemListAdapter.OnItemClick {

    private val viewModel: ItemViewModel by viewModels()

    lateinit var binding: FragmentItemBinding

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var searchView: SearchView

    private lateinit var itemListAdapter: ItemListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemBinding.bind(view)

        initView()

        observeEvents()

    }

    private fun initView() {
        (activity as ItemActivity).setSupportActionBar(binding.itemToolbar)
        setHasOptionsMenu(true)

        itemListAdapter = ItemListAdapter(this)

        binding.apply {
            itemRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = itemListAdapter.withLoadStateFooter(
                    GenericLoadStateAdapter(itemListAdapter::retry)
                )
                itemAnimator?.changeDuration = 0
            }

            addItemBtn.setOnClickListener {
                viewModel.addItem()
            }
            itemToolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }


    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.items.collectLatest { data ->
                    binding.itemRecyclerView.scrollToPosition(0)
                    itemListAdapter.submitData(data)
                }
            }
            viewModel.channelFlow.collectLatest {
                collectEvents(it)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                itemListAdapter.loadStateFlow.collectLatest { state ->
                    when (state.refresh) {
                        is LoadState.Error -> {
                            val error = (state.refresh as LoadState.Error).error
                            dataStateHandler.onDataStateChange(
                                DataState.message<Nothing>(
                                    error.message ?: getString(R.string.unknown_error_occurred)
                                )
                            )
                        }
                        is LoadState.Loading -> {
                            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                        }
                        else -> {
                            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                        }
                    }
                }
            }
        }
    }

    private fun collectEvents(e: ItemViewModel.ItemEvent) {
        when (e) {
            is ItemViewModel.ItemEvent.ShowInvalidInputMessage -> {
                dataStateHandler.onDataStateChange(
                    DataState.message<String>(
                        e.msg ?: UNKNOWN_ERROR_OCCURRED
                    )
                )
            }
            ItemViewModel.ItemEvent.CreateItemEvent -> {
                val action = ItemFragmentDirections.actionItemFragmentToAddEditItemFragment(null)
                findNavController().navigate(action)
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
        inflater.inflate(R.menu.menu_item_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search_item)
        searchView = searchItem.actionView as SearchView

        val query = viewModel.searchQuery.value
        if (query != null && query.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(query, false)
        }
        searchView.onQueryTextChanged {
            viewModel.setSearchQuery(it)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_item_by_date -> {
                viewModel.setOrderBy(Item::modifiedAt.name)
                true
            }
            R.id.action_sort_item_by_name -> {
                viewModel.setOrderBy(Item::itemName.name)
                true
            }
//            R.id.action_sync_entries -> {
//                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
//                viewModel.syncEntries()
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

    override fun onClick(item: Item) {
        TODO("Not yet implemented")
    }

}