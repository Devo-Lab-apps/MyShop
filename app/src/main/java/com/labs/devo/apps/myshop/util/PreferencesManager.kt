package com.labs.devo.apps.myshop.util

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.google.gson.Gson
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.view.util.QueryParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context, val gson: Gson) {

    private val dataStore = context.createDataStore(AppConstants.DATA_STORE_KEY)

    val introActivityShown =
        dataStore.data.map { prefs -> prefs[PreferenceKeys.introActivityShown] ?: false }

    val currentSelectedNotebook =
        dataStore.data.map { prefs ->
            val v = prefs[PreferenceKeys.currentSelectedNotebook]
                ?: FirebaseConstants.foreignNotebookKey + "$$" + FirebaseConstants.foreignNotebookName
            val s = v.split("$$")
            Pair(s[0], s[1])
        }

    val pageQueryParams =
        dataStore.data.map { prefs ->
            gson.fromJson(prefs[PreferenceKeys.pageQueryParams], QueryParams::class.java)
                ?: QueryParams()
        }

    val entryQueryParams =
        dataStore.data.map { prefs ->
            gson.fromJson(prefs[PreferenceKeys.entryQueryParams], QueryParams::class.java)
                ?: QueryParams()
        }

    suspend fun updatePageQueryParams(params: QueryParams?) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.pageQueryParams] = if (params != null) {
                gson.toJson(params)
            } else {
                gson.toJson(QueryParams())
            }
        }
    }

    suspend fun updateEntryQueryParams(params: QueryParams?) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.entryQueryParams] = if (params != null) {
                gson.toJson(params)
            } else {
                gson.toJson(QueryParams())
            }
        }
    }

    suspend fun updateCurrentSelectedNotebook(notebook: Pair<String, String>) {
        dataStore.edit { prefs ->
            val value = notebook.first + "$$" + notebook.second
            prefs[PreferenceKeys.currentSelectedNotebook] = value
        }
    }

    suspend fun updateIntoActivityShown(shown: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.introActivityShown] = shown
        }
    }

    private object PreferenceKeys {
        val introActivityShown = preferencesKey<Boolean>("intro_activity_shown")
        val currentSelectedNotebook =
            preferencesKey<String>("current_selected_notebook")
        val pageQueryParams =
            preferencesKey<String>("page_query_params")
        val entryQueryParams =
            preferencesKey<String>("entry_query_params")
    }

}