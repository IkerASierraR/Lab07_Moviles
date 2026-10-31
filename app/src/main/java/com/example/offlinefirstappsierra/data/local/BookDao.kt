package com.example.offlinefirstappsierra.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE searchQuery = :query " +
            "OR title LIKE '%' || :query || '%' " +
            "OR authorName LIKE '%' || :query || '%' " +
            " ORDER BY isFavorite DESC, lastUpdated DESC" )
    fun getBooksQuery(query:String):Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY isFavorite DESC, lastUpdated DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteBooks():Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE key = :key LIMIT 1")
    suspend fun getBookByKey(key:String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books : List<BookEntity>)

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE key = :key")
    suspend fun updateFavorite(key:String, isFavorite: Boolean): Int

    @Query("DELETE FROM books WHERE isFavorite = 0 AND searchQuery = :query")
    suspend fun deleteNonFavoritesForQuery(query:String): Int

}