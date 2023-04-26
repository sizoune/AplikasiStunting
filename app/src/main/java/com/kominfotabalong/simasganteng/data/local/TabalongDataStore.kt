package com.kominfotabalong.simasganteng.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TabalongDataStore @Inject constructor(
    private val context: Context
) : TabalongDataStoreInterface {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "tabalong_pref"
    )

    companion object {
        val LAST_SAVED_DATA_KEY = stringPreferencesKey(name = "LAST_SAVED_TABALONG")
    }

    override suspend fun saveData(data: List<Kecamatan>?, gson: Gson) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SAVED_DATA_KEY] = if (data != null) gson.toJson(data) else ""
        }
    }

    override fun getData(): Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SAVED_DATA_KEY] ?: ""
        }
}