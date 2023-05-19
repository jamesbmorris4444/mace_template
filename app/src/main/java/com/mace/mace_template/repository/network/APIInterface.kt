package com.mace.mace_template.repository.network

import com.mace.mace_template.repository.storage.Donor
import com.mace.mace_template.repository.storage.Product
import com.mace.mace_template.utils.Constants.API_KEY_REQUEST_PARAM
import com.mace.mace_template.utils.Constants.LANGUAGE_REQUEST_PARAM
import com.mace.mace_template.utils.Constants.PAGE_REQUEST_PARAM
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("movie")
    fun getDonors(
        @Query(API_KEY_REQUEST_PARAM) apiKey: String,
        @Query(LANGUAGE_REQUEST_PARAM) language: String,
        @Query(PAGE_REQUEST_PARAM) page: Int
    ): Observable<DonorResponse>
}

data class DonorResponse (
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    val results: List<Donor>,
    val products: List<List<Product>>
)