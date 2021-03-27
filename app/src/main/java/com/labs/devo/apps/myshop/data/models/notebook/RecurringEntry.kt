package com.labs.devo.apps.myshop.data.models.notebook

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "RecurringEntry")
data class RecurringEntry(
    val pageId: String = "",
    @PrimaryKey
    var recurringEntryId: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),

)
