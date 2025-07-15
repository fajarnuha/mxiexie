package com.fajarnuha.mccplus.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

const val PREFERENCES_NAME = "mccplus_settings"

expect fun createDataStore(): DataStore<Preferences>

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val selected = stringPreferencesKey("selected_access")
    private val token = stringPreferencesKey("token")

    suspend fun setSelectedAccess(value: String) {
        dataStore.edit { preferences ->
            preferences[selected] = value
        }
    }

    suspend fun getSelectedAccess(): String? {
        return dataStore.data.map { preferences ->
            preferences[selected]
        }.first()
    }

    suspend fun setToken(value: String) {
        dataStore.edit { preferences ->
            preferences[token] = value
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[token]
        }.first()
    }

}
