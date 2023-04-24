package com.kominfotabalong.simasganteng.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStore @Inject constructor(
    private val context: Context
) : UserDataStoreInterface {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_pref"
    )

    companion object {
        val LAST_SAVED_USER_KEY = stringPreferencesKey(name = "last_Saved_user")
    }

    override suspend fun saveUserData(user: LoginResponse?, gson: Gson) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SAVED_USER_KEY] = if (user != null) gson.toJson(user) else ""
        }
    }

    override fun getLoggedUserData(): Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SAVED_USER_KEY] ?: ""
        }
}