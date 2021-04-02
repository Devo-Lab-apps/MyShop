package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityMicroEntry(
    val count: Int = 0,
    val amount: Double = 0.0,
    val createdAt: MutableMap<String, Long> = mutableMapOf(),
    val recurringEntryId: String,
    val pageId: String = ""
)
