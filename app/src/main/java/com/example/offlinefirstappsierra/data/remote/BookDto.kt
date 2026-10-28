package com.example.offlinefirstappsierra.data.remote

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("key") val key:String,
    @SerializedName("title") val title:String,
    @SerializedName("author_name") val authorName:List<String>? = null,
    @SerializedName("first_publish_year") val firstPublishYear:Int?=null,
    @SerializedName("cover_i") val coverId:Long? = null,
    @SerializedName("isbn") val isbnList:List<String>?=null,
    @SerializedName("publisher") val publisherList:List<String>?=null
)
