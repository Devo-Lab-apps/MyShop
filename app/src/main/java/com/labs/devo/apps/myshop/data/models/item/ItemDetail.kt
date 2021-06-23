package com.labs.devo.apps.myshop.data.models.item

import android.os.Parcelable
import android.text.format.DateUtils.DAY_IN_MILLIS
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "itemDetail")
data class ItemDetail(
    @PrimaryKey
    val itemDetailId: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val description: String = "",
    val sortValue: Int = 0,
    val category: String? = null,
    val subCategory: String? = null,
    val boughtFrom: String? = null,
    val imageUrl: String? = null,
    val metadata: Map<String, String>? = mapOf(),
    val tags: List<String> = listOf(),
    //add 30 days from current time
    val expiresAt: Long = System.currentTimeMillis() + 30 * DAY_IN_MILLIS,
    val boughtAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
) : Parcelable
