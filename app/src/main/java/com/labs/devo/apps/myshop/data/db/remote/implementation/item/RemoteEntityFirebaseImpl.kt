package com.labs.devo.apps.myshop.data.db.remote.implementation.item

import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemService
import com.labs.devo.apps.myshop.data.db.remote.mapper.item.RemoteItemMapper
import com.labs.devo.apps.myshop.data.db.remote.models.item.RemoteEntityItem
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteEntityFirebaseImpl
@Inject constructor(val mapper: RemoteItemMapper) : RemoteItemService {

    private val itemGetLimit: Long = 10

    override suspend fun getItems(searchQuery: String, startAfter: String): List<Item> {
        val user = UserManager.user ?: throw UserNotInitializedException()
        return get(user.accountId, searchQuery, startAfter)
    }

    private suspend fun get(
        accountId: String,
        searchQuery: String,
        startAfter: String
    ): List<Item> {
        val querySnapshot = FirebaseHelper.getItemCollection(accountId)
            .orderBy(Item::itemId.name).startAfter(startAfter).limit(itemGetLimit)
            .get().await()

        return querySnapshot.map { qs ->
            val itemEntity = qs.toObject(RemoteEntityItem::class.java)
            mapper.mapFromEntity(itemEntity)
        }
    }

    override suspend fun createItem(item: Item): Item {
        return createInDb(item)
    }

    private suspend fun createInDb(item: Item): Item {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = FirebaseHelper.getItemReference(user.accountId).id
        val ref = FirebaseHelper.getItemReference(user.accountId, id)
        val data = item.copy(itemId = id)
        FirebaseHelper.runTransaction { transaction ->
            transaction.set(ref, data)
        }
        return data
    }

    override suspend fun updateItem(item: Item): Item {
        return updateInDb(item)
    }

    private suspend fun updateInDb(item: Item): Item {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val id = item.itemId
        val ref = FirebaseHelper.getItemReference(user.accountId, id)
        FirebaseHelper.runTransaction { transaction ->
            transaction.set(ref, item)
        }
        return item
    }

    override suspend fun deleteItem(itemId: String) {
        deleteFromDb(itemId)
    }

    private suspend fun deleteFromDb(itemId: String) {
        val user = UserManager.user ?: throw UserNotInitializedException()
        val ref = FirebaseHelper.getItemReference(user.accountId, itemId)
        FirebaseHelper.runTransaction { transaction ->
            transaction.delete(ref)
        }
    }
}