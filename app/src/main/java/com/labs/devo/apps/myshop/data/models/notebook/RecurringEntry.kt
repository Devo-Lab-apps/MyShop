package com.labs.devo.apps.myshop.data.models.notebook

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recurring_entry")
data class RecurringEntry(
    val pageId: String = "",
    @PrimaryKey
    val recurringEntryId: String = "",
    val entryName: String = "",
)
