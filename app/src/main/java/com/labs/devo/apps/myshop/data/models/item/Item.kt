package com.labs.devo.apps.myshop.data.models.item

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "item")
data class Item(
    @PrimaryKey
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
) : Parcelable
