package com.labs.devo.apps.myshop.data.models.account

data class Account(
    val accountId: String = "",
    val userIds: List<String> = listOf()
)
// masterUserId