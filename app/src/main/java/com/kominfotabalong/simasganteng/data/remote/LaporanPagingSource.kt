package com.kominfotabalong.simasganteng.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.data.model.LaporanResponse
import com.kominfotabalong.simasganteng.data.repository.ApiRepository

class LaporanPagingSource(
    private val repo: ApiRepository,
    private val userToken: String,
    private val status: String,
) : PagingSource<String, LaporanResponse>() {

    override fun getRefreshKey(state: PagingState<String, LaporanResponse>): String =
        "${BuildConfig.API_URL}laporan/list/$status"

    override suspend fun load(params: LoadParams<String>): LoadResult<String, LaporanResponse> {
        return when (val response =
            repo.getDataLaporan(
                userToken,
                params.key
                    ?: "${BuildConfig.API_URL}laporan/list/$status"
            )) {
            is NetworkResponse.Success -> {
                val prevKey =
                    if (response.body.data.prevPageUrl != null) {
                        response.body.data.prevPageUrl
                    } else {
                        null
                    }
                val nextKey =
                    if (response.body.data.nextPageUrl != null) {
                        response.body.data.nextPageUrl
                    } else {
                        null
                    }
                LoadResult.Page(
                    data = response.body.data.data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            is NetworkResponse.NetworkError -> {
                LoadResult.Error(response.error)
            }

            is NetworkResponse.UnknownError -> {
                LoadResult.Error(response.error)
            }

            is NetworkResponse.ServerError -> {
                LoadResult.Error(Throwable(response.body?.message))
            }
        }
    }

}