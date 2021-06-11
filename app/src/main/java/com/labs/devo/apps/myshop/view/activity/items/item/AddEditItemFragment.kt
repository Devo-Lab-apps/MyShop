package com.labs.devo.apps.myshop.view.activity.items.item

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.databinding.FragmentAddEditItemBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditItemFragment : Fragment(R.layout.fragment_add_edit_item) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: FragmentAddEditItemBinding

    private val viewModel: AddEditItemViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditItemBinding.bind(view)

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addEditItemBtn.setOnClickListener {
                if (viewModel.operation == ItemActivity.ItemConstants.ADD_ITEM_OPERATION) {
                    addOperation()
                } else {
                    editOperation()
                }
            }

            if (viewModel.operation == ItemActivity.ItemConstants.EDIT_ITEM_OPERATION) {
                addEditItemBtn.text = getString(com.labs.devo.apps.myshop.R.string.update_item)
                viewModel.item?.let { e ->
                    itemTitle.setText(e.itemName)
                    itemQuantity.setText(e.quantity.toString())
                } ?: run {
                    dataStateHandler.onDataStateChange(
                        DataState.message<Nothing>(
                            UNKNOWN_ERROR_OCCURRED_RETRY
                        )
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun editOperation() {
        binding.apply {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
            viewModel.item?.let { e ->
                val itemTitle = itemTitle.text.toString()
                val quantityText = itemQuantity.text.toString()
                try {
                    val quantity = quantityText.toDouble()
                    val prevItemDetail = ItemDetail(itemName = itemTitle, quantity = quantity)
                    val newItemDetail = ItemDetail(itemName = itemTitle, quantity = quantity)
                    viewModel.updateItem(prevItemDetail, newItemDetail)
                } catch (ex: NumberFormatException) {
                    dataStateHandler.onDataStateChange(
                        DataState.message<Nothing>(getString(com.labs.devo.apps.myshop.R.string.enter_a_valid_number))
                    )
                    addEditItemBtn.isEnabled = true
                }

            } ?: run {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        UNKNOWN_ERROR_OCCURRED_RETRY
                    )
                )
                findNavController().navigateUp()
            }
        }
    }

    private fun addOperation() {
        binding.apply {
            val itemTitle = itemTitle.text.toString()
            val quantityText = itemQuantity.text.toString()
            try {
                val quantity = quantityText.toDouble()
                val item = ItemDetail(
                    itemName = itemTitle,
                    quantity = quantity
                )
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.createItem(item)
            } catch (ex: NumberFormatException) {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(getString(com.labs.devo.apps.myshop.R.string.enter_a_valid_number))
                )
                addEditItemBtn.isEnabled = true
            }
        }
    }


    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                collectEvents(event)
            }
        }
    }

    private fun collectEvents(event: AddEditItemViewModel.AddEditItemEvent) {
        when (event) {
            is AddEditItemViewModel.AddEditItemEvent.ItemInserted -> {
                dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditItemViewModel.AddEditItemEvent.ShowInvalidInputMessage -> {
                if (event.msg != null) {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                }
            }
            is AddEditItemViewModel.AddEditItemEvent.ItemUpdated -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
            is AddEditItemViewModel.AddEditItemEvent.ItemDeleted -> {
                dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                findNavController().navigateUp()
            }
        }
        binding.addEditItemBtn.isEnabled = true
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