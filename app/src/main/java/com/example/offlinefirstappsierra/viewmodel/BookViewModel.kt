package com.example.offlinefirstappsierra.viewmodel

import android.Manifest
import android.app.Application
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.offlinefirstappsierra.data.local.AppDatabase
import com.example.offlinefirstappsierra.data.local.BookEntity
import com.example.offlinefirstappsierra.data.remote.RetrofitClient
import com.example.offlinefirstappsierra.data.repository.BookRepository
import com.example.offlinefirstappsierra.utils.NetworkUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
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

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun performSearch(query:String = _uiState.value.searchQuery){
        val cleanQuery = query.trim()
        _uiState.update {
            it.copy(
                searchQuery = cleanQuery,
                errorMessage = null
            )
        }

        observerLocalDatabase(cleanQuery,_uiState.value.filterMode)
        fetchRemoteData(cleanQuery)
    }

    fun setFilterMode(mode: FilterMode){
        _uiState.update { it.copy(filterMode =  mode) }
        observerLocalDatabase(_uiState.value.searchQuery, mode)
    }

    private fun observerLocalDatabase(query: String,mode: FilterMode){
        localDataJob?.cancel()
        localDataJob = viewModelScope.launch {
            val flow = if(mode ==FilterMode.FAVORITES){
                repository.getFavoriteBooks()
            } else {
                repository.getLocalBooks(query)
            }

            flow.collectLatest { bookList ->
                _uiState

            }

        }
    }



    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun fetchRemoteData(query: String = _uiState.value.searchQuery){
        val context = getApplication<Application>().applicationContext
        val isOnline = NetworkUtils.isNetworkAvialable(context)

        _uiState.update {it.copy(isOffline =  !isOnline) }

        if(!isOnline){
            _uiState.update { currentState ->
                val errorMsg = if(currentState.books.isEmpty()){
                    "Sin conexion a Internet. No hay datos en cache local para esta busqueda"
                } else {
                    "Sin conexion a Internet. Mostrando datos locales"
                }
                currentState.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
            return
        }

        if(query.isBlank()) return

        viewModelScope.launch {
            _uiState.update {it.copy(isLoading = true, errorMessage = null)}
            try{
                repository.refreshBooksFromApi(query)
                _uiState.update {it.copy(isLoading = false, isOffline = false)}
            } catch (e: Exception){
                _uiState.update { currentState ->
                    val isCoonIssue = e is IOException
                    val errorMsg = if(isCoonIssue){
                        "Error de red al consultar Opem Library. Monstrando datos locales"
                    } else {
                        "Error al cargar lobros: ${e.localizedMessage?: "Desconocido"}"
                    }
                    currentState.copy(
                        isLoading = false,
                        isOffline = isCoonIssue,
                        errorMessage = errorMsg
                    )
                }
            }
        }

    }

    fun toggleFavorite(book: BookEntity){
        viewModelScope.launch {
            repository.toggleFavorite(book.key, book.isFavorite)
        }
    }


    fun selectBook(book: BookEntity?){
        _uiState.update {it.copy(selectedBook = book)}
    }

    fun clearError(){
        _uiState.update {it.copy(errorMessage = null)}
    }

    class Factory(private val application: Application): ViewModelProvider.Factory{
        @Suppress("INCHECKED_CAST")
        override fun <T: ViewModel> create(modelClass: Class<T>):T{
            val db = AppDatabase.getDatabase(application)
            val repository = BookRepository(db.bookDao(), RetrofitClient.api)
            return BookViewModel(application, repository) as T
        }
    }


}