package com.kominfotabalong.simasganteng.data.model

import com.google.gson.annotations.SerializedName

open class PaginationResponse(
    @SerializedName("current_page")
    val currentPage: Int = 1,
    @SerializedName("first_page_url")
    val firstPageUrl: String? = null,
    @SerializedName("from")
    val from: Int = 1,
    @SerializedName("last_page")
    val lastPage: Int = 1,
    @SerializedName("last_page_url")
    val lastPageUrl: String? = null,
    @SerializedName("links")
    val links: List<Link>? = null,
    @SerializedName("next_page_url")
    val nextPageUrl: String? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("per_page")
    val perPage: Int = 10,
    @SerializedName("prev_page_url")
    val prevPageUrl: String? = null,
    @SerializedName("to")
    val to: Int = 1,
    @SerializedName("total")
    val total: Int = 1
)