package com.labs.devo.apps.myshop.data.models.notebook

data class Page(
    val creatorAccountId: String = "",
    val consumerAccountId: String = "",
    val creatorNotebookId: String = "",
    val consumerNotebookId: String = "",
    val pageId: String = "",
    val pageName: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
