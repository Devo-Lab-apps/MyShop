package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class EntityPage(
    val creatorAccountId: String = "",
    val consumerAccountId: String = "",
    val creatorNotebookId: String = "",
    val consumerNotebookId: String = "",
    val pageId: String = "",
    val pageName: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
