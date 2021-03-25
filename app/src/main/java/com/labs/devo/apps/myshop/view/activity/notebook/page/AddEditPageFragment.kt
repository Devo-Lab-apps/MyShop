package com.labs.devo.apps.myshop.view.activity.page.page

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.AddEditPageFragmentBinding
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.NOTEBOOK_ID
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE
import com.labs.devo.apps.myshop.view.activity.notebook.page.AddEditPageViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditPageFragment : Fragment(R.layout.add_edit_page_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: AddEditPageFragmentBinding

    private val viewModel: AddEditPageViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var operation: String

    private var page: Page? = null

    private var notebookId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditPageFragmentBinding.bind(view)
        arguments?.apply {
            page = getParcelable(PAGE)
            operation = getString(OPERATION).toString()
            notebookId = getString(NOTEBOOK_ID).toString()
        }


        initView()

        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addEditPageBtn.setOnClickListener {
                addEditPageBtn.isEnabled = false
                if (operation == ADD_PAGE_OPERATION) {
                    val pageName = pageName.text.toString()
                    val pageId = pageId.text.toString()
                    addPage(pageName, pageId)
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    val pageName = pageName.text.toString()
                    updatePage(pageName)
                }
            }
            if (operation == EDIT_PAGE_OPERATION) {
                addEditPageBtn.text = getString(R.string.update_page)
                //disable editing pageId.
                pageIdTv.visibility = View.GONE
                pageId.visibility = View.GONE
                pageId.isEnabled = false
                page?.let {
                    pageName.setText(page!!.pageName)
                } ?: run {
                    sendMessage(getString(R.string.unknown_error_occurred))
                }
            }
        }
    }

    private fun addPage(pageName: String, pageId: String) {
        UserManager.user?.let { user ->
            if (notebookId == null) {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        UserNotInitializedException().msg
                    )
                )
                findNavController().navigateUp()
                return
            }
            notebookId?.let { nid ->
                val page = Page(
                    creatorUserId = user.email,
                    consumerUserId = pageId,
                    creatorNotebookId = nid,
                    consumerNotebookId = FirebaseConstants.foreignNotebookKey,
                    pageName = pageName
                )
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.addPage(page)
            } ?: run {
                //TODO change this to a msg
                sendMessage(getString(R.string.unknown_error_occurred))
            }

        } ?: run {
            //TODO change this to a msg
            sendMessage(UserNotInitializedException().msg)
        }
    }

    private fun sendMessage(string: String) {
        dataStateHandler.onDataStateChange(
            DataState.message<Nothing>(
                string
            )
        )
        findNavController().navigateUp()
    }

    private fun updatePage(pageName: String) {
        page?.let { p ->
            val newPage = p.copy(
                pageName = pageName,
                modifiedAt = System.currentTimeMillis()
            )
            viewModel.updatePage(p, newPage)
        } ?: run {
            sendMessage(getString(R.string.unknown_error_occurred))
        }

    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is AddEditPageViewModel.AddEditPageEvent.PageInserted -> {
                        dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        findNavController().navigateUp()
                    }
                    is AddEditPageViewModel.AddEditPageEvent.ShowInvalidInputMessage -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(DataState.message<Nothing>(event.msg))
                        } else {
                            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                        }
                    }
                    is AddEditPageViewModel.AddEditPageEvent.PageUpdated -> {
                        dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                        findNavController().navigateUp()
                    }
                    is AddEditPageViewModel.AddEditPageEvent.PageDeleted -> {
                        dataStateHandler.onDataStateChange(DataState.message<String>(event.msg))
                        findNavController().navigateUp()
                    }
                }
                binding.addEditPageBtn.isEnabled = true
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