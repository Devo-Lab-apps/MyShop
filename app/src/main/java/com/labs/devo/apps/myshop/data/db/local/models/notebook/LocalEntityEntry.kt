package com.labs.devo.apps.myshop.data.db.local.models.notebook

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Entry")
data class LocalEntityEntry(
    @PrimaryKey
    val entryId: String,
    val pageId: String,
    val entryTitle: String,
    val amount: Double
)