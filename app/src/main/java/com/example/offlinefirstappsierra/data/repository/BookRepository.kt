package com.example.offlinefirstappsierra.data.repository

import com.example.offlinefirstappsierra.data.local.BookDao
import com.example.offlinefirstappsierra.data.local.BookEntity
import com.example.offlinefirstappsierra.data.remote.OpenLibraryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookRepository (
    private val bookDao: BookDao,
    private val api: OpenLibraryApi
) {
    /* Búsqueda local de datos */
    fun getLocalBooks(query: String): Flow<List<BookEntity>>{
        return if(query.trim().isEmpty()){
            bookDao.getAllBooks()
        }else {
            bookDao.getBooksQuery(query.trim())
        }
    }

    /* Obtener los libros favoritos */
    fun getFavoriteBooks(): Flow<List<BookEntity>>{
        return bookDao.getFavoriteBooks()
    }

    /* Obtener datos de OpenLibraryApi y los vamos a guardar en Room */
    suspend fun refreshBooksFromApi(query: String) = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if(cleanQuery.isEmpty()) return@withContext

        val response = api.searchBooks(cleanQuery)
        val currentTime = System.currentTimeMillis()

        val entities = response.docs.mapNotNull { dto ->
            if(dto.key.isBlank() || dto.title.isNullOrBlank()) return@mapNotNull null

            val existingBook = bookDao.getBookByKey(dto.key)
            val isFavorite = existingBook?.isFavorite?:false

            BookEntity(
                key= dto.key,
                title = dto.title?:"Sin título",
                authorName = dto.authorName?.firstOrNull()?:"Autor desconocido",
                firstPublishYear = dto.firstPublishYear ?:0,
                coverId = dto.coverId ?:0,
                isbn = dto.isbnList?.firstOrNull() ?: "",
                publisher = dto.publisherList?.firstOrNull() ?: "Editorial no especificada",
                isFavorite = isFavorite,
                searchQuery = cleanQuery,
                lastUpdated = currentTime
            )
        }

        if(entities.isNotEmpty()){
            bookDao.insertBooks(entities)
        }
    }

    /* mantenimiento de estado isFavorite */

    suspend fun toggleFavorite(key:String,currentStatus: Boolean) = withContext(Dispatchers.IO) {
        bookDao.updateFavorite(key,!currentStatus)
    }

}