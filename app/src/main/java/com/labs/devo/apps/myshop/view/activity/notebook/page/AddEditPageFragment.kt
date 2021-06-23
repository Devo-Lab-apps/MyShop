package com.labs.devo.apps.myshop.view.activity.notebook.page

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseStorageHelper.getPageUserImageUrl
import com.labs.devo.apps.myshop.business.helper.FirebaseStorageHelper.uploadFileAndGetDownloadUrl
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.AddEditPageFragmentBinding
import com.labs.devo.apps.myshop.util.StringUtils.containsSpecialChars
import com.labs.devo.apps.myshop.util.ThreadUtil.doOnMainSync
import com.labs.devo.apps.myshop.util.checkIsImageBiggerInSize
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.util.extensions.isValidEmail
import com.labs.devo.apps.myshop.util.extensions.isValidPhone
import com.labs.devo.apps.myshop.util.extensions.specialCharsNotAllowedMsg
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddEditPageFragment : Fragment(R.layout.add_edit_page_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: AddEditPageFragmentBinding

    private val viewModel: AddEditPageViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private val IMAGE_UPLOAD_REQUEST_CODE = 100000

    private lateinit var uri: Uri

    private lateinit var downloadUrl: String

    // coroutine handler to handle exceptions
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        CoroutineScope(Dispatchers.Main).launch {
            if (exception is UIException) {
                if (exception.navigateUp) {
                    sendMessageAndNavigateUp(exception.msg)
                } else {
                    dataStateHandler.onDataStateChange(DataState.message<Nothing>(exception.msg))
                }

            } else {
                dataStateHandler.onDataStateChange(
                    DataState.message<Nothing>(
                        exception.message ?: getString(R.string.unknown_error_occurred)
                    )
                )
            }
            if (::binding.isInitialized) binding.addEditPageBtn.isEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditPageFragmentBinding.bind(view)

        initView()

        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addUserDetailsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                userDetailsForm.isVisible = isChecked
            }
            addEditPageBtn.setOnClickListener {
                addEditPageBtn.isEnabled = false
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                if (viewModel.operation == ADD_PAGE_OPERATION) {
                    val pageName = pageName.text.toString()
                    val consumerId = consumerUserId.text.toString()
                    addPage(pageName, consumerId)
                } else {
                    val pageName = pageName.text.toString()
                    updatePage(pageName)
                }
            }
            if (viewModel.operation == EDIT_PAGE_OPERATION) {
                editPageOperation()
            }
            addPictureBtn.setOnClickListener {
                val cameraIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                cameraIntent.type = "image/*"
                if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(cameraIntent, IMAGE_UPLOAD_REQUEST_CODE)
                }
            }
        }
    }

    private fun editPageOperation() {
        binding.apply {
            addEditPageBtn.text = getString(R.string.update_page)
            //disable editing pageId.
            pageIdTv.visibility = View.GONE
            consumerUserId.visibility = View.GONE
            consumerUserId.isEnabled = false
            viewModel.page?.let { page ->
                pageName.setText(page.pageName)
                userPhoneNumber.setText(page.userPhoneNumber ?: "")
                userAddress.setText(page.userAddress ?: "")
                if (page.userImageUrl != null) {
                    Glide.with(requireContext()).load(page.userImageUrl).into(userImage)
                }
            } ?: sendMessageAndNavigateUp(getString(R.string.unknown_error_occurred))
        }
    }


    private fun addPage(pageName: String, consumerId: String) {
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
            val user = UserManager.user ?: throw UIException(UserNotInitializedException().msg)
            val page: Page = doOnMainSync(Dispatchers.Main) {
                getPageForAdd(user, pageName, consumerId)
            }
            validateAddPageInputs(page)
            checkAndUploadImage(user.accountId, consumerId)
            val isDownloadUrlInitialized = this@AddEditPageFragment::downloadUrl.isInitialized
            val userDownloadUrl = if (isDownloadUrlInitialized) downloadUrl else null
            val pageWithImage = page.copy(userImageUrl = userDownloadUrl)
            viewModel.addPage(pageWithImage)

        }
    }

    private fun updatePage(pageName: String) {
        CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
            val user =
                UserManager.user ?: throw UIException(UserNotInitializedException().msg, true)
            val p = viewModel.page ?: throw UIException(
                getString(R.string.unknown_error_occurred),
                true
            )
            val page: Page = doOnMainSync(Dispatchers.Main) { getPageForUpdate(pageName, p) }
            validateEditPageInputs(p, page)
            checkAndUploadImage(user.accountId, p.consumerUserId)
            val userDownloadUrl = if (this@AddEditPageFragment::downloadUrl.isInitialized) {
                downloadUrl
            } else if (!page.userImageUrl.isNullOrEmpty()) {
                page.userImageUrl
            } else null
            val pageWithImage = page.copy(userImageUrl = userDownloadUrl)
            viewModel.updatePage(pageWithImage)
        }
    }

    private fun getPageForAdd(
        user: User,
        pageName: String,
        consumerUserId: String,
    ): Page {
        if (viewModel.notebookId == null) {
            throw UIException(UserNotInitializedException().msg, true)
        }

        val notebookId = viewModel.notebookId
            ?: throw UIException(getString(R.string.unknown_error_occurred), true)

        val userPhoneNumber = if (
            binding.userPhoneNumber.text.toString().isEmpty()
        ) null
        else binding.userPhoneNumber.text.toString()

        val userAddress = if (
            binding.userAddress.text.toString().isEmpty()
        ) null
        else binding.userAddress.text.toString()

        return Page(
            creatorUserId = user.email,
            consumerUserId = consumerUserId,
            creatorNotebookId = notebookId,
            consumerNotebookId = FirebaseConstants.foreignNotebookKey,
            pageName = pageName,
            userPhoneNumber = userPhoneNumber,
            userAddress = userAddress,
        )
    }

    private fun getPageForUpdate(
        pageName: String,
        page: Page
    ): Page {
        if (viewModel.notebookId == null) {
            throw UIException(UserNotInitializedException().msg, true)
        }

        val userPhoneNumber = if (
            binding.userPhoneNumber.text.toString().isEmpty()
        ) null
        else binding.userPhoneNumber.text.toString()

        val userAddress = if (
            binding.userAddress.text.toString().isEmpty()
        ) null
        else binding.userAddress.text.toString()

        return page.copy(
            pageName = pageName,
            userPhoneNumber = userPhoneNumber,
            userAddress = userAddress,
            modifiedAt = System.currentTimeMillis()
        )
    }

    private suspend fun checkAndUploadImage(userId: String, consumerId: String) {
        val isUriInitialized = doOnMainSync(Dispatchers.Main) {
            this@AddEditPageFragment::uri.isInitialized
        }
        if (isUriInitialized) {
            uploadImage(userId, consumerId)
        }
    }

    private suspend fun uploadImage(userId: String, consumerId: String) {
        checkIsImageBiggerInSize(
            uri,
            100000,
            UIException("Image size should be smaller than ${100} KB")
        )
        downloadUrl =
            uploadFileAndGetDownloadUrl(
                getPageUserImageUrl(userId, consumerId),
                uri,
                UIException::class.java
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_UPLOAD_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.data != null) {
                try {
                    uri = data.data!!
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val selectedImg = BitmapFactory.decodeStream(inputStream)
                    binding.userImage.setImageBitmap(selectedImg)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "An error occurred!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "You didn't pick an image!", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "You didn't pick an image!", Toast.LENGTH_LONG)
                .show()
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


    private fun validateAddPageInputs(page: Page) {

        if (page.pageName.length > 20) {
            throw UIException("Name should be less than 20 characters.")
        }

        if (page.pageId.isNotBlank()) {
            throw UIException("Please provide valid inputs")
        }

        if (containsSpecialChars(page.pageName)) {
            throw UIException(specialCharsNotAllowedMsg("page name"))
        }

        if (!page.consumerUserId.isValidEmail()) {
            throw UIException("Customer email id is not a valid email.")
        }


        if (!page.userPhoneNumber.isValidPhone()) {
            throw UIException("Phone number is not valid")
        }

    }

    private fun validateEditPageInputs(prevPage: Page, newPage: Page) {

        if (newPage.pageName.length > 20) {
            throw UIException("Name should be less than 20 characters.")
        }

        if (newPage.pageId.isBlank()) {
            throw UIException("Please provide valid inputs")
        }

        if (containsSpecialChars(prevPage.pageName)) {
            throw UIException(specialCharsNotAllowedMsg("page name"))
        }

        if (!newPage.userPhoneNumber.isValidPhone()) {
            throw UIException("Phone number is not valid")
        }

        if (!newPage.consumerUserId.isValidEmail()) {
            throw UIException("Customer email id is not correctly formatted.")
        }
    }

}

data class UIException(val msg: String, val navigateUp: Boolean = false) : java.lang.Exception(msg)