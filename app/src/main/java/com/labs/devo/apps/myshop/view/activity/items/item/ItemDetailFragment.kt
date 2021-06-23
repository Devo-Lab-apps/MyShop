package com.labs.devo.apps.myshop.view.activity.items.item

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.databinding.ItemDetailFragmentBinding
import com.labs.devo.apps.myshop.util.ThreadUtil
import com.labs.devo.apps.myshop.util.ThreadUtil.doOnMain
import com.labs.devo.apps.myshop.view.activity.items.item.ItemActivity.ItemConstants.EDIT_ITEM_OPERATION
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import kotlinx.coroutines.flow.collect


class ItemDetailFragment : Fragment(R.layout.item_detail_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: ItemDetailFragmentBinding

    private val viewModel: ItemDetailViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    val categories = listOf("Other", "Electric", "Eatables")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ItemDetailFragmentBinding.bind(view)

        initView()
        observeEvents()
    }

    private fun initView() {
        viewModel.getItemDetail()
        binding.editItemBtn.isEnabled = false
        binding.editItemBtn.setOnClickListener {
            viewModel.editItem()
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvents(event)
            }
        }
    }

    private suspend fun collectEvents(event: ItemDetailViewModel.ItemDetailEvent) {
        when (event) {
            ItemDetailViewModel.ItemDetailEvent.ItemDetailFound -> {
                doOnMain {
                    populateUIFromItem()
                    binding.editItemBtn.isEnabled = true
                }
            }
            ItemDetailViewModel.ItemDetailEvent.InvalidItem -> {
                sendMessageAndNavigateUp(UNKNOWN_ERROR_OCCURRED_RETRY)
            }
            ItemDetailViewModel.ItemDetailEvent.EditItemEvent -> {
                val action = ItemFragmentDirections.actionItemFragmentToAddEditItemFragment(
                    viewModel.item,
                    EDIT_ITEM_OPERATION,
                    viewModel.itemDetail
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun populateUIFromItem() {
        binding.apply {
            val itemDetail = viewModel.itemDetail
            itemName.text = itemDetail.itemName
            itemQuantity.text = itemDetail.quantity.toString()
            itemDescription.text = itemDetail.description
            if (itemDetail.boughtFrom.isNullOrEmpty()) {
                itemBoughtFrom.visibility = View.GONE
                itemBoughtFromTv.visibility = View.GONE
            } else {
                itemBoughtFrom.text = itemDetail.boughtFrom
            }
            if (itemDetail.category.isNullOrEmpty()) {
                itemCategory.visibility = View.GONE
                itemCategoryTv.visibility = View.GONE
            } else {
                if (categories.indexOf(itemDetail.category) != -1) {
                    itemCategory.text = itemDetail.category
                } else {
                    itemCategory.visibility = View.GONE
                    itemCategoryTv.visibility = View.GONE
                }
            }
            if (itemDetail.subCategory.isNullOrEmpty()) {
                itemSubcategory.visibility = View.GONE
                itemSubcategoryTv.visibility = View.GONE
            } else {
                itemSubcategory.text = itemDetail.category
            }
            if (itemDetail.imageUrl != null) {
                Glide.with(itemImage).load(itemDetail.imageUrl).into(itemImage)
            }
        }
    }

    private fun sendMessageAndNavigateUp(string: String) {
        dataStateHandler.onDataStateChange(
            DataState.message<Nothing>(
                string
            )
        )
        findNavController().navigateUp()
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