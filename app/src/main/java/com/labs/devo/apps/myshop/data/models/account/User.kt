package com.labs.devo.apps.myshop.data.models.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User class that stores the profile of the user.
 */
@Parcelize
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val accountId: String = "",
    var loggedInDeviceId: String = "",
    val profileImageUrl: String = "",
    var loggedInAt: Long = 0,
    val signedUpInAt: Long = 0,
    val permissions: List<Int> = listOf(),
    @field:JvmField val isMasterUser: Boolean = true
    //TODO add other profile attributes
) : Parcelable