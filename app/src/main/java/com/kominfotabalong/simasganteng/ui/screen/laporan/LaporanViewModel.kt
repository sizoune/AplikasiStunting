package com.kominfotabalong.simasganteng.ui.screen.laporan

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.data.model.AddLaporanRequest
import com.kominfotabalong.simasganteng.data.model.ApiBaseResponse
import com.kominfotabalong.simasganteng.data.model.BalitaResponse
import com.kominfotabalong.simasganteng.data.remote.BalitaPagingSource
import com.kominfotabalong.simasganteng.data.remote.LaporanPagingSource
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val _myLat: MutableStateFlow<Double> = MutableStateFlow((-1).toDouble())
    val myLat: StateFlow<Double>
        get() = _myLat

    private val _myLng: MutableStateFlow<Double> = MutableStateFlow((-1).toDouble())
    val myLng: StateFlow<Double>
        get() = _myLng

    private val _myAddr: MutableStateFlow<String> =
        MutableStateFlow("")
    val myAddr: StateFlow<String>
        get() = _myAddr

    private val _isFinishSearching = MutableStateFlow(false)
    val isFinishSearching get() = _isFinishSearching

    fun setSearchingStatustoTrue() = viewModelScope.launch {
        _isFinishSearching.emit(true)
    }

    private val _addLaporanState: MutableStateFlow<UiState<ApiBaseResponse>> =
        MutableStateFlow(UiState.Loading)
    val addLaporanState: StateFlow<UiState<ApiBaseResponse>>
        get() = _addLaporanState

    private val _updateLaporanState: MutableStateFlow<UiState<ApiBaseResponse>> =
        MutableStateFlow(UiState.Loading)
    val updateLaporanState: StateFlow<UiState<ApiBaseResponse>>
        get() = _updateLaporanState

    private val _nikState: MutableStateFlow<UiState<BalitaResponse>> =
        MutableStateFlow(UiState.Loading)
    val nikState: StateFlow<UiState<BalitaResponse>>
        get() = _nikState

    private val _locLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val locLoading: StateFlow<Boolean>
        get() = _locLoading


    var dataCollect = mutableStateOf(0)

    fun collectData(dataIndex: Int) {
        println("currentStep = $dataIndex")
        dataCollect.value = dataIndex
    }

    fun setLatLng(latLng: LatLng) {
        viewModelScope.launch {
            _myLat.emit(latLng.latitude)
            _myLng.emit(latLng.longitude)
        }
    }

    fun addLaporan(token: String, dataLaporan: AddLaporanRequest) {
        viewModelScope.launch {
            _addLaporanState.emit(UiState.Loading)
            apiRepository.tambahLaporan(token, dataLaporan).catch {
                _addLaporanState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _addLaporanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _updateLaporanState.value = UiState.Unauthorized
                        } else {
                            _addLaporanState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _addLaporanState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _addLaporanState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun updateLaporan(token: String, laporanID: Int, status: String) {
        viewModelScope.launch {
            _updateLaporanState.emit(UiState.Loading)
            apiRepository.updateStatusLaporan(token, laporanID, status).catch {
                _updateLaporanState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        println("sukses")
                        _updateLaporanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _updateLaporanState.value = UiState.Unauthorized
                        } else {
                            _updateLaporanState.emit(
                                UiState.Error(
                                    response.body?.message
                                        ?: "Terjadi kesalahan saat memproses data"
                                )
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _updateLaporanState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _updateLaporanState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    fun cariDataAnak(token: String, nik: String) {
        viewModelScope.launch {
            _nikState.emit(UiState.Loading)
            apiRepository.cekNikBalita(token, nik).catch {
                _nikState.emit(UiState.Error(it.message.toString()))
            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _nikState.value = UiState.Success(response.body.body.data)
                    }

                    is NetworkResponse.ServerError -> {
                        when (response.code) {
                            401 -> {
                                _nikState.value = UiState.Unauthorized
                            }

                            404 -> {
                                println("NIK Not Found ${response.body?.message}")
                                _nikState.value = UiState.Success(BalitaResponse(nik_anak = nik))
                            }

                            else -> {
                                _nikState.emit(
                                    UiState.Error(
                                        response.body?.message
                                            ?: "Terjadi kesalahan saat memproses data"
                                    )
                                )
                            }
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _nikState.emit(
                            UiState.Error(
                                response.error.localizedMessage
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _nikState.emit(
                            UiState.Error(
                                response.error.localizedMessage ?: "Unknown Error"
                            )
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context) {
        viewModelScope.launch {
            _locLoading.emit(true)
        }
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                viewModelScope.launch {
                    _locLoading.emit(false)
                    if (location == null)
                        trackUserLoc(context)
                    else {
                        _myLat.emit(location.latitude)
                        _myLng.emit(location.longitude)
                    }
                }
            }

    }

    @SuppressLint("MissingPermission")
    fun trackUserLoc(context: Context) {
        viewModelScope.launch {
            _locLoading.emit(true)
        }
        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMaxUpdates(1)
                .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        viewModelScope.launch {
                            _locLoading.emit(false)
                            _myLat.emit(location.latitude)
                            _myLng.emit(location.longitude)
                        }
                    }
                }
            }
        }
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(locationRequest, locationCallback, null)
    }


    @Suppress("DEPRECATION")
    fun getAddressFromLocation(context: Context, latLng: LatLng) {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            println("addresses = ${addresses.size}")
            _myAddr.value = addresses[0]?.let { getAddressText(it) } ?: ""
        }
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                // declare here the geocodeListener, as it requires Android API 33
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1, geocodeListener)
            } else {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                addresses?.let {
                    _myAddr.value = getAddressText(addresses[0]) ?: ""
                }
                println("addresses = $addresses")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getAddressText(address: Address): String? {
        var addressText: String? = ""
        val maxAddressLineIndex = address.maxAddressLineIndex
        for (i in 0..maxAddressLineIndex) {
            addressText += address.getAddressLine(i)
            if (i != maxAddressLineIndex) {
                addressText += "\n"
            }
        }
        return addressText
    }

    fun getLaporan(userToken: String, status: String) = Pager(PagingConfig(pageSize = 30)) {
        LaporanPagingSource(apiRepository, userToken, status)
    }.flow.cachedIn(viewModelScope)

    fun getDaftarBalita(userToken: String) = Pager(PagingConfig(pageSize = 30)) {
        BalitaPagingSource(apiRepository, userToken)
    }.flow.cachedIn(viewModelScope)
}