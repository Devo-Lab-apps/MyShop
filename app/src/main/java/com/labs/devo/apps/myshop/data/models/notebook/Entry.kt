package com.labs.devo.apps.myshop.data.models.notebook

import android.os.Parcelable
import com.labs.devo.apps.myshop.const.AppConstants
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Entry(
    val pageId: String = "",
    val entryId: String = "",
    val entryTitle: String = "",
    val entryDescription: String = "",
    val entryAmount: Double = 0.0,
    val isRepeating: Boolean = false,
    val entryMetadata: Map<String, String> = mutableMapOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
): Parcelable {
    val entryCreatedAt: String = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US).format(Date(createdAt))
}
//isRepeatingEntry
//metadata
    // -timeOfRepeat,
    // -frequencyOfRepetition,
    // -notificationId,
    // -isPaid
    // -repeatEndsAt
    // -automaticallyCreateEntryEveryday