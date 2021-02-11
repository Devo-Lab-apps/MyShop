package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityNotebook(
    val notebookId: String = "",

    val notebookName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis(),

    val pages: MutableList<String> = mutableListOf()
)