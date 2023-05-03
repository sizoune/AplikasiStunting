package com.kominfotabalong.simasganteng.ui.screen.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.TabalongGeoJsonModel
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val gson: Gson,
    private val application: Application,
) : ViewModel() {
    private val _tabalongState: MutableStateFlow<UiState<TabalongGeoJsonModel>> =
        MutableStateFlow(UiState.Loading)
    val tabalongState: StateFlow<UiState<TabalongGeoJsonModel>>
        get() = _tabalongState.asStateFlow()

    fun getTabalongGeoData() = viewModelScope.launch {
        _tabalongState.value = UiState.Loading
        try {
            val jsonString = application.resources.openRawResource(R.raw.tabalong).bufferedReader()
                .use { it.readText() }
            val random = Random()
            val convertedData = gson.fromJson(jsonString, TabalongGeoJsonModel::class.java)
            convertedData.features.map {
                it.fillColor = android.graphics.Color.argb(
                    255,
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
                )
            }
            _tabalongState.value =
                UiState.Success(convertedData)
        } catch (ex: Exception) {
            ex.printStackTrace()
            _tabalongState.value =
                UiState.Error(ex.message.toString())
        }
    }
}