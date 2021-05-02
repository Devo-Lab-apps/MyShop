package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityPage(
    val creatorAccountId: String = "",
    val consumerAccountId: String = "",
    val creatorNotebookId: String = "",
    val consumerNotebookId: String = "",
    val pageId: String = "",
    val pageName: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val userPhoneNumber: String? = null,
    val userAddress: String? = null,
    val userImageUrl: String? = null
)
