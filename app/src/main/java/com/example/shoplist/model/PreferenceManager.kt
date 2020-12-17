package com.example.shoplist.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

enum class SortOrder {BY_NAME,BY_DATE}

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore("user_preference")
    val preferenceFlow = dataStore.data
        .map {preferences ->
            val sortOrder = SortOrder.valueOf(preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_NAME.name)
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSaveSortOrder(sortOrder: SortOrder) {
        dataStore.edit {preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateSaveHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }
}

private object PreferencesKeys {
    val SORT_ORDER = preferencesKey<String>("sort_order")
    val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
}