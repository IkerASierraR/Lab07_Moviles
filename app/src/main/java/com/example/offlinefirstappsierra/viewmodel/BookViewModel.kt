package com.example.offlinefirstappsierra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.offlinefirstappsierra.data.repository.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BookViewModel(
    application: Application,
    private val repository: BookRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState : StateFlow<BookUiState> = _uiState

    private var localDataJob : Job? = null

    init {
        performSearch(_uiState.value.searchQuery)
    }

    fun onQueryChange(newQuery:String){
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun performSearch(query:String = _uiState.value.searchQuery){
        val cleanQuery = query.trim()
        _uiState.update {
            it.copy(
                searchQuery = cleanQuery,
                errorMessage = null
            )
        }
    }



}