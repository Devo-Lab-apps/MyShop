package com.labs.devo.apps.myshop.data.models.notebook

data class MicroEntry(
    val count: Int = 1,
    val amount: Double = 0.0,
    val createdAt: MutableMap<String, Long> = mutableMapOf(),
    val recurringEntryId: String,
    val pageId: String = "",
    )
