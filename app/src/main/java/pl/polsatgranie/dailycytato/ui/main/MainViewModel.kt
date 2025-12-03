package pl.polsatgranie.dailycytato.ui.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.data.model.Quote
import pl.polsatgranie.dailycytato.data.repository.QuoteRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel dla MainActivity - zarządza cytatem dnia
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuoteRepository(application.applicationContext)
    
    private val _currentQuote = MutableLiveData<Quote?>()
    val currentQuote: LiveData<Quote?> = _currentQuote
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    fun loadTodaysQuote() {
        Log.d(TAG, "loadTodaysQuote: Rozpoczynam ładowanie cytatu")
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            Log.d(TAG, "loadTodaysQuote: Użytkownik: ${currentUser?.email}, Kategorie: ${currentUser?.preferredCategories}")
            
            val quote = repository.getRandomQuote(currentUser?.preferredCategories)
            
            if (quote != null) {
                Log.d(TAG, "loadTodaysQuote: Otrzymano cytat: ${quote.content}")
                _currentQuote.value = quote
            } else {
                Log.e(TAG, "loadTodaysQuote: Nie otrzymano cytatu z API")
                _errorMessage.value = "Brak połączenia z internetem"
            }
            _isLoading.value = false
        }
    }
    
    fun loadNewQuote() {
        Log.d(TAG, "loadNewQuote: Rozpoczynam ładowanie nowego cytatu")
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            Log.d(TAG, "loadNewQuote: Użytkownik: ${currentUser?.email}, Kategorie: ${currentUser?.preferredCategories}")
            
            val quote = repository.getRandomQuote(currentUser?.preferredCategories)
            
            if (quote != null) {
                Log.d(TAG, "loadNewQuote: Otrzymano cytat: ${quote.content}")
                _currentQuote.value = quote
            } else {
                Log.e(TAG, "loadNewQuote: Nie otrzymano cytatu z API")
                _errorMessage.value = "Brak połączenia z internetem"
            }
            _isLoading.value = false
        }
    }
    
    companion object {
        private const val TAG = "MainViewModel"
    }
}