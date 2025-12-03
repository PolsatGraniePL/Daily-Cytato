package pl.polsatgranie.dailycytato.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import pl.polsatgranie.dailycytato.data.repository.QuoteRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuoteRepository(application.applicationContext)
    
    suspend fun createUser(): Boolean = repository.createUserFromFirebaseAuth()
}