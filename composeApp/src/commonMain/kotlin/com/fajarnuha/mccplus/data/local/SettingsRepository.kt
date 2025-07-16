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

    private val cache = stringPreferencesKey("access_cache")

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

    fun getTokenFlow() = dataStore.data.map { it[token] }

    suspend fun setToken(value: String?) {
        dataStore.edit { preferences ->
            if (value == null) preferences.remove(token)
            else preferences[token] = value
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[token]
        }.first()
    }

    suspend fun setAccessCache(value: String) {
        dataStore.edit { preferences ->
            preferences[cache] = value
        }
    }

    suspend fun getAccessCache(): String? {
        return dataStore.data.map { preferences ->
            preferences[cache]
        }.first()
    }

}
