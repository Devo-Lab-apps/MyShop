package com.labs.devo.apps.myshop.helper

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.labs.devo.apps.myshop.const.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore(AppConstants.DATA_STORE_KEY)

    val introActivityShown =
        dataStore.data.map { prefs -> prefs[PreferenceKeys.introActivityShown] ?: false }


    suspend fun updateIntoActivityShown(shown: Boolean) {
        dataStore.edit { prefs->
            prefs[PreferenceKeys.introActivityShown] = shown
        }
    }

    private object PreferenceKeys {
        val introActivityShown = preferencesKey<Boolean>("intro_activity_shown")
    }

}