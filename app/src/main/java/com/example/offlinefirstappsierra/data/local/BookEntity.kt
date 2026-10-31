package com.example.offlinefirstappsierra.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val key: String,
    val title: String,
    val authorName: String,
    val firstPublishYear:Int,
    val coverId: Long,
    val isbn: String,
    val publisher: String,
    val isFavorite: Boolean = false,
    val searchQuery: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
){
    val coverUrl: String?
        get() = if (coverId > 0){
            "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
        }else null
}
