package com.labs.devo.apps.myshop.data.models.notebook

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notebook(
    var notebookId: String = "",

    val notebookName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis(),

    val pages: List<String> = listOf(),

    val creatorUserId: String = "",

    val accountId: String = "",

    val metadata: Map<String, String> = mapOf()

) : Parcelable
// metadata
//isImported, isForeign, importerUserId, importedAt