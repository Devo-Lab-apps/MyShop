package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.const.Permissions
import com.labs.devo.apps.myshop.util.exceptions.PermissionRequiredException
import com.labs.devo.apps.myshop.util.exceptions.UserNotInitializedException
import java.util.*

object PermissionsHelper {

    /**
     * Check certain permissions for the current user.
     */
    fun checkPermissions(vararg permissions: Permissions) {
        val user = UserManager.user ?: throw UserNotInitializedException()
        //master user have all permissions
        if (user.isMasterUser) return
        for (permission in permissions) {
            if (!user.permissions.contains(permission.ordinal)) {
                throw PermissionRequiredException(normalizePermission(permission))
            }
        }
    }

    /**
     * Convert permission name to readable string.
     */
    private fun normalizePermission(permission: Permissions): String {
        val tokens = permission.name.split("_")
        return tokens.joinToString(" ") { token ->
            token.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
        }
    }


}