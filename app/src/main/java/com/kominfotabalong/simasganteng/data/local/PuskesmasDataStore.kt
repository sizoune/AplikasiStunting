package com.kominfotabalong.simasganteng.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PuskesmasDataStore @Inject constructor(
    private val context: Context
) : PuskesmasDataStoreInterface {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "puskes_pref"
    )

    companion object {
        val LAST_SAVED_DATA_PUSKES = stringPreferencesKey(name = "LAST_SAVED_PUSKESMAS")
    }

    override suspend fun saveDataPuskes(data: List<PuskesmasResponse>?, gson: Gson) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SAVED_DATA_PUSKES] = if (data != null) gson.toJson(data) else ""
        }
    }

    override fun getDataPuskes(): Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SAVED_DATA_PUSKES] ?: ""
        }
}