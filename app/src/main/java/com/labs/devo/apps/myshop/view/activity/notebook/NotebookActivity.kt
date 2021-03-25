package com.labs.devo.apps.myshop.view.activity.notebook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.databinding.ActivityNotebookBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotebookActivity : AppCompatActivity(), DataStateListener {

    private lateinit var binding: ActivityNotebookBinding

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notebook)

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
        binding.notebookProgressBar.isVisible = isVisible
    }

    object NotebookConstants {
        const val OPERATION = "operation"
        const val ADD_NOTEBOOK_OPERATION = "add_notebook"
        const val EDIT_NOTEBOOK_OPERATION = "edit_notebook"
        const val ADD_PAGE_OPERATION = "add_page"
        const val EDIT_PAGE_OPERATION = "edit_page"
        const val ADD_ENTRY_OPERATION = "add_entry"
        const val EDIT_ENTRY_OPERATION = "edit_entry"
        const val NOTEBOOK_ID = "notebook_id"
        const val PAGE_ID = "page_id"
        const val PAGE_NAME = "page_name"
        const val NOTEBOOK = "notebook"
        const val PAGE = "page"
        const val ENTRY = "entry"
        const val PAGES_NOT_IMPORTED_ERR =
            "Pages for this account are not imported yet. Please sync notebooks if pages are already imported."
    }

}