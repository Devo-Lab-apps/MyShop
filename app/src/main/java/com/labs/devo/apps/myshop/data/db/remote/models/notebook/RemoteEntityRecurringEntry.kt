package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityRecurringEntry(
    val pageId: String = "",
    var recurringEntryId: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: String = "Daily",
    val recurringTime: String = "",
    val amount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val fetchedAt: Long = System.currentTimeMillis()
)