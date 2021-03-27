package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityEntry(
    var entryId: String = "",
    val pageId: String = "",
    val entryTitle: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
