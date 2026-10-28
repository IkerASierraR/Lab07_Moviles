package com.example.offlinefirstappsierra.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int=30
    ): OpenLibrarySearchResponse
}