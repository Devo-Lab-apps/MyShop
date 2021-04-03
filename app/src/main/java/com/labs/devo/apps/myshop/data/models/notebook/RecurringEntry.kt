package com.labs.devo.apps.myshop.data.models.notebook

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(tableName = "RecurringEntry")
data class RecurringEntry(
    val pageId: String = "",
    @PrimaryKey
    var recurringEntryId: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: String = "Daily",
    val recurringTime: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),

    ) : Parcelable
