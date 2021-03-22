package com.labs.devo.apps.myshop.data.models.notebook

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "page")
data class Page(
    val creatorUserId: String = "",
    val consumerUserId: String = "",
    val creatorNotebookId: String = "",
    val consumerNotebookId: String = "",
    @PrimaryKey
    val pageId: String = "",
    val pageName: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
): Parcelable
//creatorUserName, creatorAccountId, consumerAccountId, pageContactNumber, pageDisplayImage, isSubscriber
//metadata
    // -cycleDuration, foreignPageName