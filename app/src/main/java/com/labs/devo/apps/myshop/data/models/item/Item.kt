package com.labs.devo.apps.myshop.data.models.item

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
