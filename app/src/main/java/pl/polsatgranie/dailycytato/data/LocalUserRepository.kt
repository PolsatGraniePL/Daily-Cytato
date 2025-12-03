package pl.polsatgranie.dailycytato.data

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import pl.polsatgranie.dailycytato.data.model.User
import java.io.File
import java.io.IOException

/**
 * Lokalne zarządzanie danymi użytkownika bez Firestore
 * Używa SharedPreferences i lokalnych plików JSON
 */
class LocalUserRepository(private val context: Context) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("daily_cytato_prefs", Context.MODE_PRIVATE)
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val userAdapter: JsonAdapter<User> = moshi.adapter(User::class.java)
    private val categoriesAdapter: JsonAdapter<List<String>> = 
        moshi.adapter(Types.newParameterizedType(List::class.java, String::class.java))
    private val usersMapAdapter: JsonAdapter<Map<String, User>> = 
        moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, User::class.java))
    
    private val usersFile = File(context.filesDir, "users.json")
    
    /**
     * Zapisuje użytkownika do lokalnego pliku JSON
     */
    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val users = loadAllUsers().toMutableMap()
            users[user.uid] = user
            
            val usersJson = usersMapAdapter.toJson(users)
            
            usersFile.writeText(usersJson)
            
            // Zapisz aktualnego użytkownika w SharedPreferences
            sharedPrefs.edit()
                .putString("current_user_uid", user.uid)
                .putString("current_user_json", userAdapter.toJson(user))
                .apply()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Ładuje użytkownika z lokalnych danych
     */
    suspend fun getCurrentUser(): User? {
        return try {
            val userJson = sharedPrefs.getString("current_user_json", null)
            userJson?.let { userAdapter.fromJson(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Ładuje użytkownika po UID
     */
    suspend fun getUserByUid(uid: String): User? {
        return try {
            val users = loadAllUsers()
            users[uid]
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Zapisuje preferencje kategorii użytkownika
     */
    suspend fun saveUserCategories(uid: String, categories: List<String>): Result<Unit> {
        return try {
            val user = getUserByUid(uid)
            if (user != null) {
                val updatedUser = user.copy(preferredCategories = categories)
                saveUser(updatedUser)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Pobiera preferencje kategorii użytkownika
     */
    suspend fun getUserCategories(uid: String): List<String> {
        return try {
            getUserByUid(uid)?.preferredCategories ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Usuwa dane aktualnego użytkownika (wylogowanie)
     */
    suspend fun clearCurrentUser(): Result<Unit> {
        return try {
            sharedPrefs.edit()
                .remove("current_user_uid")
                .remove("current_user_json")
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sprawdza czy użytkownik jest zalogowany
     */
    fun isUserLoggedIn(): Boolean {
        return sharedPrefs.contains("current_user_uid")
    }
    
    /**
     * Zapisuje cytat dnia do cache
     */
    fun saveQuoteOfTheDay(quote: String, author: String) {
        val timestamp = System.currentTimeMillis()
        sharedPrefs.edit()
            .putString("cached_quote", quote)
            .putString("cached_author", author)
            .putLong("cached_quote_timestamp", timestamp)
            .apply()
    }
    
    /**
     * Pobiera zapisany cytat dnia (jeśli jest z dzisiaj)
     */
    fun getCachedQuoteOfTheDay(): Pair<String, String>? {
        val timestamp = sharedPrefs.getLong("cached_quote_timestamp", 0)
        val oneDayInMillis = 24 * 60 * 60 * 1000
        
        return if (System.currentTimeMillis() - timestamp < oneDayInMillis) {
            val quote = sharedPrefs.getString("cached_quote", null)
            val author = sharedPrefs.getString("cached_author", null)
            if (quote != null && author != null) {
                Pair(quote, author)
            } else null
        } else null
    }
    
    /**
     * Ładuje wszystkich użytkowników z pliku
     */
    private fun loadAllUsers(): Map<String, User> {
        return try {
            if (!usersFile.exists()) {
                emptyMap()
            } else {
                val json = usersFile.readText()
                usersMapAdapter.fromJson(json) ?: emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}