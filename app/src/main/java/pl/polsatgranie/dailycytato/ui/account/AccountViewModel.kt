package pl.polsatgranie.dailycytato.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.polsatgranie.dailycytato.data.model.User
import pl.polsatgranie.dailycytato.data.repository.QuoteRepository

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuoteRepository(application.applicationContext)
    
    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData
    
    fun loadUserData(uid: String) {
        viewModelScope.launch {
            _userData.value = repository.getUserData(uid)
        }
    }
    
    suspend fun signOut() = repository.signOut()
}