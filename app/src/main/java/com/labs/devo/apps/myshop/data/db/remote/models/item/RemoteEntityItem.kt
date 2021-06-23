package com.labs.devo.apps.myshop.data.db.remote.models.item

data class RemoteEntityItem(
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)