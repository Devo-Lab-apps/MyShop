package com.labs.devo.apps.myshop.data.models.util

import androidx.room.Entity

@Entity(
    primaryKeys = ["operationName"]
)
data class RateLimit(
    val operationName: String,
    var count: Int,
    var timestamps: MutableList<Long> = mutableListOf()
)