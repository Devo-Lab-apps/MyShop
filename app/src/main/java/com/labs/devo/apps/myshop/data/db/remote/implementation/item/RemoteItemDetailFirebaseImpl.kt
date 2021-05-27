package com.labs.devo.apps.myshop.data.db.remote.implementation.item

import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.ErrorCode
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemDetailService
import com.labs.devo.apps.myshop.data.db.remote.mapper.item.RemoteItemDetailMapper
import com.labs.devo.apps.myshop.data.db.remote.models.item.RemoteEntityItemDetail
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.util.exceptions.ItemDetailNotFoundException
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val itemDetailGetLimit = 10

class RemoteItemDetailEntityFirebaseImpl
@Inject constructor(val mapper: RemoteItemDetailMapper) : RemoteItemDetailService {

    override suspend fun getItemDetails(
        searchQuery: String,
        startAfter: String?
    ): List<ItemDetail> {
        val user = UserManager.user ?: throw UserNotInitializedException()
        return get(user.accountId, searchQuery, startAfter)
    }

    private suspend fun get(
        accountId: String,
        searchQuery: String,
        startAfter: String?
    ): List<ItemDetail> {
        //TODO search query not used
        val start = startAfter?.toLong() ?: 0L
        val querySnapshot = FirebaseHelper.getItemCollection(accountId)
            .orderBy(Item::modifiedAt.name).startAfter(start).limit(itemDetailGetLimit.toLong())
            .get().await()

        return querySnapshot.map { qs ->
            val itemEntity = qs.toObject(RemoteEntityItemDetail::class.java)
            mapper.mapFromEntity(itemEntity)
        }
    }

    override suspend fun createItemDetail(itemDetail: ItemDetail): ItemDetail {
        return createInDb(itemDetail)
    }

    private suspend fun createInDb(itemDetail: ItemDetail): ItemDetail {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = FirebaseHelper.getItemReference(user.accountId).id
        val ref = FirebaseHelper.getItemReference(user.accountId, id)
        val data = itemDetail.copy(itemDetailId = id)
        FirebaseHelper.runTransaction { transaction ->
            transaction.set(ref, data)
        }
        return data
    }

    override suspend fun updateItemDetail(itemDetail: ItemDetail): ItemDetail {
        return updateInDb(itemDetail)
    }

    private suspend fun updateInDb(itemDetail: ItemDetail): ItemDetail {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = itemDetail.itemId
        val ref = FirebaseHelper.getItemReference(user.accountId, id)
        FirebaseHelper.runTransaction { transaction ->
            val existing = transaction.get(ref)
            if (existing.exists()) {
                transaction.set(ref, itemDetail)
            } else {
                throw ItemDetailNotFoundException(ErrorCode.ERROR_ITEM_DETAIL_NOT_FOUND)
            }
        }
        return itemDetail
    }

    override suspend fun deleteItemDetail(itemDetailId: String) {
        FirebaseHelper.runTransaction { transaction ->
            deleteFromDb(itemDetailId, transaction)
        }
    }

    private fun deleteFromDb(itemDetailId: String, transaction: Transaction) {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val ref = FirebaseHelper.getItemReference(user.accountId, itemDetailId)
        val existing = transaction.get(ref)
        if (existing.exists()) {
            transaction.delete(ref)
        } else {
            throw ItemDetailNotFoundException(ErrorCode.ERROR_ITEM_DETAIL_NOT_FOUND)
        }
    }
}