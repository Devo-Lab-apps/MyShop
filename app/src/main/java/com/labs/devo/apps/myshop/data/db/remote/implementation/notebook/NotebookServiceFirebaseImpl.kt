package com.labs.devo.apps.myshop.data.db.remote.implementation.notebook

import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.NotebookService
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.NotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.EntityNotebook
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotebookServiceFirebaseImpl @Inject constructor(
    val mapper: NotebookMapper
) : NotebookService {
    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getNotebooks(): Flow<List<Notebook>> {
        return channelFlow {
            val user = UserManager.user ?: throw Exception("User not initialized")

            val querySnapshot = FirebaseHelper.getNotebookCollection(user.accountId)
                .get().await()

            offer(querySnapshot.documents.map { doc ->
                mapper.mapFromEntity(doc.toObject(EntityNotebook::class.java)!!)
            })
        }
    }
}