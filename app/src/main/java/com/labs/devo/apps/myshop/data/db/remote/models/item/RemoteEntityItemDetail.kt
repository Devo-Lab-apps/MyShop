package com.labs.devo.apps.myshop.data.db.remote.models.item

import android.text.format.DateUtils

data class RemoteEntityItemDetail(
    val itemDetailId: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val description: String = "",
    val sortValue: Int = 0,
    val category: String? = null,
    val subCategory: String? = null,
    val boughtFrom: String? = null,
    val imageUrl: String? = null,
    val metadata: Map<String, String>? = mapOf(),
    val tags: List<String> = listOf(),
    //add 30 days from current time
    val expiresAt: Long = System.currentTimeMillis() + 30 * DateUtils.DAY_IN_MILLIS,
    val boughtAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)