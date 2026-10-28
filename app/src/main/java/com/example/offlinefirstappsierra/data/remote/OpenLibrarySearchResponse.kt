package com.example.offlinefirstappsierra.data.remote

import com.google.gson.annotations.SerializedName

data class OpenLibrarySearchResponse(
    @SerializedName("numFound") val numFound:Int,
    @SerializedName("start") val start:Int,
    @SerializedName("docs") val docs:List<BookDto> = emptyList()
)
