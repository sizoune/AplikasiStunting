package com.kominfotabalong.simasganteng.ui.screen.laporan

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.kominfotabalong.simasganteng.data.model.Kecamatan
import com.kominfotabalong.simasganteng.data.model.PuskesmasResponse
import com.kominfotabalong.simasganteng.data.model.ResponseListObject
import com.kominfotabalong.simasganteng.data.repository.ApiRepository
import com.kominfotabalong.simasganteng.ui.common.UiState
import com.kominfotabalong.simasganteng.util.showToast
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
    private val _myLat: MutableStateFlow<Double> =
        MutableStateFlow((-1).toDouble())
    val myLat: StateFlow<Double>
        get() = _myLat

    private val _myLng: MutableStateFlow<Double> =
        MutableStateFlow((-1).toDouble())
    val myLng: StateFlow<Double>
        get() = _myLng

    private val _myAddr: MutableStateFlow<String> =
        MutableStateFlow("")
    val myAddr: StateFlow<String>
        get() = _myAddr

    private val _kecamatanState: MutableStateFlow<UiState<ResponseListObject<Kecamatan>>> =
        MutableStateFlow(UiState.Loading)
    val kecamatanState: StateFlow<UiState<ResponseListObject<Kecamatan>>>
        get() = _kecamatanState

    private val _pkmState: MutableStateFlow<UiState<ResponseListObject<PuskesmasResponse>>> =
        MutableStateFlow(UiState.Loading)
    val pkmState: StateFlow<UiState<ResponseListObject<PuskesmasResponse>>>
        get() = _pkmState

    private val _addLaporanState: MutableStateFlow<UiState<ApiBaseResponse>> =
        MutableStateFlow(UiState.Loading)
    val addLaporanState: StateFlow<UiState<ApiBaseResponse>>
        get() = _addLaporanState

    private val _isLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    fun getTabalongDistricts(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            apiRepository.getTabalongDistricts(token).catch {
                _isLoading.value = false
                _kecamatanState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isLoading.value = false
                        _kecamatanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isLoading.value = false
                        if (response.code == 401) {
                            _kecamatanState.value = UiState.Unauthorized
                        } else {
                            _kecamatanState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _isLoading.value = false
                        _kecamatanState.value = UiState.Error(
                            response.error.localizedMessage ?: "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isLoading.value = false
                        _kecamatanState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    fun getDaftarPuskes(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            apiRepository.getDaftarPuskes(token).catch {
                _isLoading.value = false
                _pkmState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _isLoading.value = false
                        _pkmState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        _isLoading.value = false
                        if (response.code == 401) {
                            _kecamatanState.value = UiState.Unauthorized
                        } else {
                            _kecamatanState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _isLoading.value = false
                        _pkmState.value = UiState.Error(
                            response.error.localizedMessage ?: "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _isLoading.value = false
                        _pkmState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    fun addLaporan(token: String, dataLaporan: AddLaporanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            apiRepository.tambahLaporan(token, dataLaporan).catch {
                _isLoading.value = false
                _addLaporanState.value = UiState.Error(it.message.toString())

            }.collect { response ->
                _isLoading.value = false
                when (response) {
                    is NetworkResponse.Success -> {
                        _addLaporanState.value = UiState.Success(response.body.body)
                    }

                    is NetworkResponse.ServerError -> {
                        if (response.code == 401) {
                            _addLaporanState.value = UiState.Unauthorized
                        } else {
                            _addLaporanState.value = UiState.Error(
                                response.body?.message
                                    ?: "Terjadi kesalahan saat memproses data"
                            )
                        }
                    }

                    is NetworkResponse.NetworkError -> {
                        _addLaporanState.value = UiState.Error(
                            response.error.localizedMessage ?: "Tolong periksa koneksi anda!"
                        )
                    }

                    is NetworkResponse.UnknownError -> {
                        _addLaporanState.value = UiState.Error(
                            response.error.localizedMessage
                                ?: "Unknown Error"
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context) {
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    trackUserLoc(context)
                else {
                    _myLat.value = location.latitude
                    _myLng.value = location.longitude
                }

            }
    }

    @SuppressLint("MissingPermission")
    fun trackUserLoc(context: Context) {
        viewModelScope.launch {
            val locationRequest: LocationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .setMaxUpdates(1)
                    .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location != null) {
                            _myLat.value = location.latitude
                            _myLng.value = location.longitude
                        }
                    }
                }
            }
            LocationServices
                .getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, locationCallback, null)
        }
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
            ex.localizedMessage?.let { context.showToast(it) }
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
}