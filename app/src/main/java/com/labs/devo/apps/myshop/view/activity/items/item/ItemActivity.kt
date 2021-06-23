package com.labs.devo.apps.myshop.view.activity.items.item

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.ActivityItemBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemActivity : AppCompatActivity(), DataStateListener {
    object ItemConstants {

        const val ITEM_DETAIL: String = "item_detail"
        const val ITEM: String = "item"
        const val EDIT_ITEM_OPERATION: String = "edit_item"
        const val ADD_ITEM_OPERATION: String = "add_item"
    }

    lateinit var binding: ActivityItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item)

    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            // Handle loading
            showProgressBar(dataState.loading)

            // Handle Message
            dataState.message?.let { event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    /**
     * Show toast with the msg.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show progress bar.
     */
    private fun showProgressBar(isVisible: Boolean) {
        binding.itemProgressBar.isVisible = isVisible
    }
}