package com.labs.devo.apps.myshop.data.models.notebook

import com.labs.devo.apps.myshop.const.AppConstants
import java.text.SimpleDateFormat
import java.util.*

data class Entry(
    val pageId: String,
    val entryId: String,
    val entryTitle: String,
    val entryDescription: String,
    val entryAmount: Double,
    val isRepeating: Boolean,
    val entryMetadata: Map<String, String>,
    val createdAt: Long,
    val modifiedAt: Long
) {
    val entryCreatedAt: String = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US).format(Date(createdAt))
}