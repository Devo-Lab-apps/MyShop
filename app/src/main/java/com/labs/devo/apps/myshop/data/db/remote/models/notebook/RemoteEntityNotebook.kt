package com.labs.devo.apps.myshop.data.db.remote.models.notebook

data class RemoteEntityNotebook(
    val notebookId: String = "",

    val notebookName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis(),

    val pages: MutableList<String> = mutableListOf(),

    val creatorUserId: String = "",

    val accountId: String = "",

    val metadata: Map<String, String> = mapOf()
)

object NotebookMetadataConstants {
    const val isForeign = "isForeign"
    const val isImported = "isImported"
    const val importerUserId = "importerUserId"

}