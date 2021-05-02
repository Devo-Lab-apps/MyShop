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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseStorageHelper.uploadFileAndGetDownloadUrl
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.AddEditPageFragmentBinding
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.ADD_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.EDIT_PAGE_OPERATION
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class AddEditPageFragment : Fragment(R.layout.add_edit_page_fragment) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: AddEditPageFragmentBinding

    private val viewModel: AddEditPageViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private val IMAGE_UPLOAD_REQUEST_CODE = 100000

    private lateinit var uri: Uri

    private lateinit var downloadUrl: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddEditPageFragmentBinding.bind(view)

        initView()

        observeEvents()
    }

    private fun initView() {
        binding.apply {
            addUserDetailsCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    binding.userDetailsForm.visibility = View.GONE
                } else {
                    binding.userDetailsForm.visibility = View.VISIBLE
                }
            }
            addEditPageBtn.setOnClickListener {
                addEditPageBtn.isEnabled = false
                if (viewModel.operation == ADD_PAGE_OPERATION) {
                    val pageName = pageName.text.toString()
                    val consumerId = consumerUserId.text.toString()
                    addPage(pageName, consumerId)
                } else {
                    dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                    val pageName = pageName.text.toString()
                    updatePage(pageName)
                }
            }
            if (viewModel.operation == EDIT_PAGE_OPERATION) {
                addEditPageBtn.text = getString(R.string.update_page)
                //disable editing pageId.
                pageIdTv.visibility = View.GONE
                consumerUserId.visibility = View.GONE
                consumerUserId.isEnabled = false
                userPhoneNumber.setText(viewModel.page!!.userPhoneNumber ?: "")
                userAddress.setText(viewModel.page!!.userAddress ?: "")
                if (viewModel.page!!.userImageUrl != null) {
                    Glide.with(requireContext()).load(viewModel.page!!.userImageUrl).into(userImage)
                }
                viewModel.page?.let {
                    pageName.setText(viewModel.page!!.pageName)
                } ?: run {
                    sendMessageAndNavigateUp(getString(R.string.unknown_error_occurred))
                }
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

    private fun addPage(pageName: String, consumerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserManager.user
            if (user == null) {
                sendMessageAndNavigateUp(UserNotInitializedException().msg)
                return@launch
            }
            showLoadingOnMain()
            checkAndUploadImage(user.email, consumerId)
            doOnMain {
                val page = validateAndGetUserOnAdd(user, pageName, consumerId)
                if (page != null) viewModel.addPage(page)
            }

        }
    }

    private fun validateAndGetUserOnAdd(
        user: User,
        pageName: String,
        consumerUserId: String,
    ): Page? {
        if (viewModel.notebookId == null) {
            sendMessageAndNavigateUp(UserNotInitializedException().msg)
            return null
        }
        val isDownloadUrlInitialized = this@AddEditPageFragment::downloadUrl.isInitialized
        val notebookId = viewModel.notebookId
        if (notebookId == null) {
            sendMessageAndNavigateUp(getString(R.string.unknown_error_occurred))
            return null
        }

        val userPhoneNumber = if (
            binding.userPhoneNumber.text.toString().isEmpty()
        ) null
        else binding.userPhoneNumber.text.toString()

        val userAddress = if (
            binding.userAddress.text.toString().isEmpty()
        ) null
        else binding.userAddress.text.toString()

        val userDownloadUrl = if (isDownloadUrlInitialized) downloadUrl else null

        return Page(
            creatorUserId = user.email,
            consumerUserId = consumerUserId,
            creatorNotebookId = notebookId,
            consumerNotebookId = FirebaseConstants.foreignNotebookKey,
            pageName = pageName,
            userPhoneNumber = userPhoneNumber,
            userAddress = userAddress,
            userImageUrl = userDownloadUrl
        )
    }

    private fun validateAndGetUserOnUpdate(
        pageName: String,
        page: Page
    ): Page? {
        if (viewModel.notebookId == null) {
            sendMessageAndNavigateUp(UserNotInitializedException().msg)
            return null
        }
        val notebookId = viewModel.notebookId
        if (notebookId == null) {
            sendMessageAndNavigateUp(getString(R.string.unknown_error_occurred))
            return null
        }

        val userPhoneNumber = if (
            binding.userPhoneNumber.text.toString().isEmpty()
        ) null
        else binding.userPhoneNumber.text.toString()

        val userAddress = if (
            binding.userAddress.text.toString().isEmpty()
        ) null
        else binding.userAddress.text.toString()

        val userDownloadUrl = if (this@AddEditPageFragment::downloadUrl.isInitialized) {
            downloadUrl
        } else if (!page.userImageUrl.isNullOrEmpty()) {
            page.userImageUrl
        } else null

        return page.copy(
            pageName = pageName,
            userPhoneNumber = userPhoneNumber,
            userAddress = userAddress,
            userImageUrl = userDownloadUrl,
            modifiedAt = System.currentTimeMillis()
        )
    }

    private suspend fun checkAndUploadImage(userId: String, consumerId: String) {
        val isUriInitialized: Boolean
        withContext(Dispatchers.Main) {
            isUriInitialized = this@AddEditPageFragment::uri.isInitialized
        }
        if (isUriInitialized) {
            uploadImage(userId, consumerId)
        }
    }

    private suspend fun uploadImage(userId: String, consumerId: String) {
        val result = uploadFileAndGetDownloadUrl("/user/$userId/pages/$consumerId/profile-img/", uri)
        val urlOrError = result.data?.getContentIfNotHandled()
        if (urlOrError?.startsWith("https://") == true) {
            downloadUrl = urlOrError
        } else {
            toastOnMain(urlOrError ?: "An error occurred while uploading the image", true)
        }
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

    private suspend fun toastOnMain(msg: String, enableAddButton: Boolean) {
        withContext(Dispatchers.Main) {
            binding.addEditPageBtn.isEnabled = enableAddButton
            dataStateHandler.onDataStateChange(
                DataState.message<Nothing>(msg)
            )
        }
    }

    private suspend fun doOnMain(f: () -> Unit) {
        withContext(Dispatchers.Main) {
            f.invoke()
        }
    }

    private suspend fun showLoadingOnMain() {
        withContext(Dispatchers.Main) {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
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

    private fun updatePage(pageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserManager.user
            if (user == null) {
                sendMessageAndNavigateUp(UserNotInitializedException().msg)
                return@launch
            }
            viewModel.page?.let { p ->
                checkAndUploadImage(user.email, p.consumerUserId)
                doOnMain {
                    val page = validateAndGetUserOnUpdate(pageName, p) ?: return@doOnMain
                    viewModel.updatePage(p, page)
                }
            } ?: doOnMain {
                sendMessageAndNavigateUp(getString(R.string.unknown_error_occurred))
            }
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