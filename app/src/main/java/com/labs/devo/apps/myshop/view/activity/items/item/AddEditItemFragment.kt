package com.labs.devo.apps.myshop.view.activity.items.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.FirebaseStorageHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.ErrorCode
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.databinding.FragmentAddEditItemBinding
import com.labs.devo.apps.myshop.util.ThreadUtil.doOnMainSync
import com.labs.devo.apps.myshop.util.checkIsImageBiggerInSize
import com.labs.devo.apps.myshop.util.exceptions.ExceptionCatcher
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import com.labs.devo.apps.myshop.view.activity.notebook.page.UIException
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditItemFragment : Fragment(R.layout.fragment_add_edit_item) {

    private val IMAGE_UPLOAD_REQUEST_CODE: Int = 100000

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private lateinit var binding: FragmentAddEditItemBinding

    private val viewModel: AddEditItemViewModel by viewModels()

    private lateinit var dataStateHandler: DataStateListener

    private lateinit var uri: Uri

    private lateinit var downloadUrl: String

    val categories = listOf("Other", "Electric", "Eatables")

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
            if (::binding.isInitialized) binding.addEditItemBtn.isEnabled = true
        }
    }

    val subCategories = mapOf(
        "Other" to listOf("A", "B"),
        "Electric" to listOf("C", "D"),
        "Eatables" to listOf("E", "F")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditItemBinding.bind(view)

        initView()
        observeEvents()
    }

    private fun initView() {
        binding.apply {
            itemExtraDetails.setOnCheckedChangeListener { _, isChecked ->
                itemExtraDetailsForm.isVisible = isChecked
            }

            addEditItemBtn.setOnClickListener {
                addEditItemBtn.isEnabled = false
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                if (viewModel.operation == ItemActivity.ItemConstants.ADD_ITEM_OPERATION) {
                    addOperation()
                } else {
                    editOperation()
                }
            }

            itemImage.setOnClickListener {
                val cameraIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                cameraIntent.type = "image/*"
                if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(cameraIntent, IMAGE_UPLOAD_REQUEST_CODE)
                }
            }

            itemCategory.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )

            itemCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val subCategoriesAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        subCategories[categories[position]] ?: listOf()
                    )
                    (itemSubcategory).adapter = subCategoriesAdapter
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            if (viewModel.operation == ItemActivity.ItemConstants.EDIT_ITEM_OPERATION) {
                addEditItemBtn.text = getString(R.string.update_item)
                //Show progress bar for fetching Item Detail
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                populateUIFromItemDetail()
            }
        }
    }

    private fun populateUIFromItemDetail() {
        binding.apply {
            val itemDetail = viewModel.itemDetail!!
            itemName.setText(itemDetail.itemName)
            itemQuantity.setText(itemDetail.quantity.toString())
            itemDescription.setText(itemDetail.description)
            itemBoughtFrom.setText(itemDetail.boughtFrom ?: "")
            itemDetail.category?.let {
                if (categories.indexOf(it) != -1) {
                    itemCategory.setSelection(categories.indexOf(it))
                }
            }
            itemDetail.subCategory?.let {
                if (subCategories.containsKey(it) && subCategories[it]?.indexOf(it) != -1) {
                    itemSubcategory.setSelection(subCategories[it]?.indexOf(it) ?: 0)
                }
            }
            if (itemDetail.imageUrl != null) {
                Glide.with(itemImage).load(itemDetail.imageUrl).into(itemImage)
            }
        }
    }

    private fun editOperation() {
        binding.apply {
            dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
            viewModel.item?.let { e ->
                CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                    val user =
                        UserManager.user ?: throw UIException(
                            UserNotInitializedException().msg,
                            true
                        )
                    val prevItemDetail = viewModel.itemDetail!!
                    checkAndUploadImage(user.accountId, prevItemDetail.itemId)
                    val itemImageUrl = if (this@AddEditItemFragment::downloadUrl.isInitialized) {
                        downloadUrl
                    } else if (!prevItemDetail.imageUrl.isNullOrEmpty()) {
                        prevItemDetail.imageUrl
                    } else null
                    val newItemDetail = getItemDetailFromUi(
                        prevItemDetail.itemId,
                        prevItemDetail.itemDetailId,
                        itemImageUrl
                    )
                    viewModel.updateItem(prevItemDetail, newItemDetail)
                }
            } ?: run {
                sendMessageAndNavigateUp(UNKNOWN_ERROR_OCCURRED_RETRY)
            }
        }
    }

    private fun addOperation() {
        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val user = UserManager.user ?: throw UIException(UserNotInitializedException().msg)
                val id = FirebaseHelper.getItemReference(user.accountId).id
                checkAndUploadImage(user.accountId, id)
                val isDownloadUrlInitialized = this@AddEditItemFragment::downloadUrl.isInitialized
                val userDownloadUrl = if (isDownloadUrlInitialized) downloadUrl else null
                val itemDetail = getItemDetailFromUi(id, "", userDownloadUrl)
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.createItem(itemDetail)
            }
        }
    }

    private suspend fun getItemDetailFromUi(
        itemId: String = "",
        itemDetailId: String = "",
        downloadUrl: String? = null
    ): ItemDetail =
        doOnMainSync {
            val quantityText = binding.itemQuantity.text.toString()
            val quantity = try {
                quantityText.toDouble()
            } catch (ex: Exception) {
                throw ExceptionCatcher.GenericException(
                    ErrorCode.ERROR_UNKNOWN_STATE,
                    "Quantity should be number."
                )
            }
            return@doOnMainSync ItemDetail(
                itemId = itemId,
                itemDetailId = itemDetailId,
                itemName = binding.itemName.text.toString(),
                quantity = quantity,
                description = binding.itemDescription.text.toString(),
                boughtFrom = binding.itemBoughtFrom.text.toString(),
                category = binding.itemCategory.selectedItem?.toString(),
                subCategory = binding.itemSubcategory.selectedItem?.toString(),
                imageUrl = downloadUrl
            )
        }

    private suspend fun checkAndUploadImage(userId: String, itemId: String) {
        val isUriInitialized = doOnMainSync(Dispatchers.Main) {
            this@AddEditItemFragment::uri.isInitialized
        }
        if (isUriInitialized) {
            uploadImage(userId, itemId)
        }
    }

    private suspend fun uploadImage(userId: String, itemId: String) {
        checkIsImageBiggerInSize(
            uri,
            100000,
            UIException("Image size should be smaller than ${100} KB")
        )
        downloadUrl =
            FirebaseStorageHelper.uploadFileAndGetDownloadUrl(
                FirebaseStorageHelper.getItemImageUrl(userId, itemId),
                uri,
                UIException::class.java
            )
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
                sendMessageAndNavigateUp(event.msg)
            }
            is AddEditItemViewModel.AddEditItemEvent.ItemDeleted -> {
                sendMessageAndNavigateUp(event.msg)
            }
        }
        binding.addEditItemBtn.isEnabled = true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_UPLOAD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                try {
                    uri = data.data!!
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val selectedImg = BitmapFactory.decodeStream(inputStream)
                    binding.itemImage.setImageBitmap(selectedImg)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }

    }


}