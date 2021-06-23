package com.labs.devo.apps.myshop.business.helper

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.tasks.await

object FirebaseStorageHelper {

    private val storageRef = FirebaseStorage.getInstance().reference


    suspend fun uploadFile(filePath: String, uri: Uri): DataState<String> {
        return try {
            storageRef.child(filePath).putFile(uri).await()
            DataState.data("") //TODO change msg
        } catch (ex: Exception) {
            DataState.message(ex.message ?: "An error occurred")
        }
    }

    suspend fun <T : Exception> uploadFileAndGetDownloadUrl(
        filePath: String,
        uri: Uri,
        exceptionClass: Class<T>
    ): String {
        return try {
            val ref = storageRef.child(filePath)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (ex: Exception) {
            val exception = exceptionClass.getDeclaredConstructor(exceptionClass)
                .newInstance(ex.message ?: "An error occurred")
            throw exception
        }
    }

    suspend fun uploadFile(filePath: String, byteArray: ByteArray): DataState<String> {
        return try {
            storageRef.child(filePath).putBytes(byteArray).await()
            DataState.data("") //TODO change this
        } catch (ex: Exception) {
            DataState.message(ex.message ?: "An error occurred")
        }
    }

    fun getPageUserImageUrl(userId: String, consumerId: String): String {
        return "/user/$userId/pages/$consumerId/profile-img/"
    }

    fun getItemImageUrl(userId: String, itemId: String): String {
        return "/user/$userId/items/$itemId/item-image/"
    }

}