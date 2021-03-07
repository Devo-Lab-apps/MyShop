package com.labs.devo.apps.myshop.data.db.local.models.notebook

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull


@Entity(tableName = "Notebook")
data class LocalEntityNotebook(

    @PrimaryKey
    val notebookId: String = "",

    @NotNull
    val notebookName: String = "",

    @NotNull
    val createdAt: Long = System.currentTimeMillis(),

    @NotNull
    val modifiedAt: Long = System.currentTimeMillis(),

    val pages: List<String> = listOf(),

    @NotNull
    val creatorUserId: String = "",

    @NotNull
    val accountId: String = "",

    val metadata: Map<String, String> = mapOf()
)