package com.labs.devo.apps.myshop.data.db.local.models.notebook

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "Page"
)
data class LocalEntityPage(
    val creatorAccountId: String = "",
    val consumerAccountId: String = "",
    val creatorNotebookId: String = "",
    val consumerNotebookId: String = "",

    @PrimaryKey
    val pageId: String = "",

    val pageName: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)