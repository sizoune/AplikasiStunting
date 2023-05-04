package com.kominfotabalong.simasganteng.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.NetworkResponse
import com.kominfotabalong.simasganteng.BuildConfig
import com.kominfotabalong.simasganteng.data.model.BalitaResponse
import com.kominfotabalong.simasganteng.data.repository.ApiRepository

class BalitaPagingSource(
    private val repo: ApiRepository,
    private val userToken: String,
) : PagingSource<String, BalitaResponse>() {

    override fun getRefreshKey(state: PagingState<String, BalitaResponse>): String =
        "${BuildConfig.API_URL}balita/list"

    override suspend fun load(params: LoadParams<String>): LoadResult<String, BalitaResponse> {
        return when (val response =
            repo.getDaftarBalita(
                userToken,
                params.key
                    ?: "${BuildConfig.API_URL}balita/list"
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