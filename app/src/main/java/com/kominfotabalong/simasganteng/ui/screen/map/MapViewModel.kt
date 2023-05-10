package com.kominfotabalong.simasganteng.ui.screen.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.R
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.model.SebaranResponse
import com.kominfotabalong.simasganteng.data.model.TabalongGeoJsonModel
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val gson: Gson,
    private val application: Application,
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val _sebaranState: MutableStateFlow<UiState<ResponseListObject<SebaranResponse>>> =
        MutableStateFlow(UiState.Loading)
    val sebaranState: StateFlow<UiState<ResponseListObject<SebaranResponse>>>
        get() = _sebaranState.asStateFlow()

    private val _tabalongState: MutableStateFlow<UiState<TabalongGeoJsonModel>> =
        MutableStateFlow(UiState.Loading)
    val tabalongState: StateFlow<UiState<TabalongGeoJsonModel>>
        get() = _tabalongState.asStateFlow()

    val clickedItemCluster = MutableStateFlow(SebaranResponse())
    fun setClickedItemCluster(clicked: SebaranResponse) = viewModelScope.launch {
        clickedItemCluster.emit(clicked)
    }

    fun getDataSebaran(token: String, tahun: String, bulan: String?, status: String? = null) {
        viewModelScope.launch {
            _sebaranState.emit(UiState.Loading)
            apiRepository.getDataSebaran(token, tahun, bulan, status).catch {
                _sebaranState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _sebaranState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _sebaranState.value = UiState.Unauthorized
                        } else {
                            _sebaranState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _sebaranState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _sebaranState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

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