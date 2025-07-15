package com.fajarnuha.mccplus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.lang.IllegalStateException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

internal object ContextHolder {
    private var appContext: Context? = null

    fun getContext(): Context {
        return appContext ?: throw IllegalStateException("Context not initialized")
    }

    fun setContext(context: Context) {
        appContext = context
    }
}

actual fun createDataStore(): DataStore<Preferences> {
    return ContextHolder.getContext().dataStore
}
