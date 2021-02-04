package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class EntityNotebook(
    val notebookId: String = "",

    val notebookName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis(),

    val pages: List<String> = listOf()
)