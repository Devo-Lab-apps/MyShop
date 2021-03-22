package com.labs.devo.apps.myshop.data.models.notebook

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notebook")
data class Notebook(
    @PrimaryKey
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