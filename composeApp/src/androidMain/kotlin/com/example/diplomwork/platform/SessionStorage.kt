package com.example.diplomwork.platform

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session")

actual class SessionStorage(private val context: Context) {

    private val KEY_EMPLOYEE_ID = stringPreferencesKey("employee_id")
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_TABEL = stringPreferencesKey("tabel_number")

    actual suspend fun saveSession(employeeId: String, name: String, tabelNumber: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMPLOYEE_ID] = employeeId
            prefs[KEY_NAME] = name
            prefs[KEY_TABEL] = tabelNumber
        }
    }

    actual suspend fun getEmployeeId(): String? {
        return context.dataStore.data.first()[KEY_EMPLOYEE_ID]
    }

    actual suspend fun getName(): String? {
        return context.dataStore.data.first()[KEY_NAME]
    }

    actual suspend fun getTabelNumber(): String? {
        return context.dataStore.data.first()[KEY_TABEL]
    }

    actual suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}