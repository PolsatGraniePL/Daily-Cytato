package pl.polsatgranie.dailycytato.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.data.model.Category
import pl.polsatgranie.dailycytato.data.repository.QuoteRepository

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuoteRepository(application.applicationContext)
    
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess
    
    private var currentUserId: String = ""
    
    fun loadPreferences(uid: String) {
        currentUserId = uid
        viewModelScope.launch {
            val user = repository.getUserData(uid)
            val userCategories = user?.preferredCategories ?: emptyList()
            
            _categories.value = Category.getAvailableCategories().map { category ->
                category.copy(isSelected = userCategories.contains(category.id))
            }
        }
    }
    
    fun toggleCategory(category: Category) {
        _categories.value = _categories.value?.map {
            if (it.id == category.id) it.copy(isSelected = !it.isSelected) else it
        }
    }
    
    fun savePreferences() {
        viewModelScope.launch {
            val selectedCategories = _categories.value
                ?.filter { it.isSelected }
                ?.map { it.id } ?: emptyList()
            
            val success = repository.updateUserPreferences(currentUserId, selectedCategories)
            _saveSuccess.value = success
        }
    }
}
