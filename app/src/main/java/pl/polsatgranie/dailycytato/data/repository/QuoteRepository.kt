package pl.polsatgranie.dailycytato.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import pl.polsatgranie.dailycytato.data.LocalUserRepository
import pl.polsatgranie.dailycytato.data.api.QuoteApiService
import pl.polsatgranie.dailycytato.data.model.Quote
import pl.polsatgranie.dailycytato.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Repository obsługujące pobieranie cytatów z API Quotify
 */
class QuoteRepository(private val context: Context) {
    
    private val auth = FirebaseAuth.getInstance()
    private val localUserRepository = LocalUserRepository(context)
    
    private val quoteApi = Retrofit.Builder()
        .baseUrl(QuoteApiService.BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()
        .create(QuoteApiService::class.java)
    
    /**
     * Pobiera losowy cytat z API
     * @param tags Lista preferowanych kategorii użytkownika
     * @return Quote? - null jeśli brak połączenia
     */
    suspend fun getRandomQuote(tags: List<String>? = null): Quote? {
        return try {
            Log.d(TAG, "getRandomQuote: Rozpoczynam pobieranie cytatu. Tags: $tags")

            //            if (!tags.isNullOrEmpty()) {
            Log.d(TAG, "getRandomQuote: Pobieram cytat z tagiem: ${tags?.first()}")
    //                val response = quoteApi.getQuotesByTag(tags.first())
            val response = quoteApi.getRandomQuote()

            Log.d(TAG, "getRandomQuote: Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            Log.d(TAG, "getRandomQuote: Response body: ${response.body()}")

            val quote = response.body()?.quotes?.random()

//            val q2ote = quote.(Quote::class.java)

            quote
    //                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
    //                    val quote = response.body()!!.random() // Losowy cytat z listy
    //                    Log.d(TAG, "getRandomQuote: Sukces! Otrzymano cytat: ${quote.content}")
    //                    quote
    //                } else {
    //                    Log.e(TAG, "getRandomQuote: Response error: ${response.errorBody()?.string()}")
    //                    null
    //                }
    //            } else {
    //                Log.d(TAG, "getRandomQuote: Pobieram losowy cytat (bez tagów)")
    //                val response = quoteApi.getRandomQuote()
    //
    //                Log.d(TAG, "getRandomQuote: Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
    //                Log.d(TAG, "getRandomQuote: Response body: ${response.body()}")
    //
    ////                if (response.isSuccessful && response.body() != null) {
    ////                    val quote = response.body()!!
    ////                    Log.d(TAG, "getRandomQuote: Sukces! Otrzymano cytat: ${quote.content}")
    ////                    quote
    ////                } else {
    ////                    Log.e(TAG, "getRandomQuote: Response error: ${response.errorBody()?.string()}")
    ////                    null
    ////                }
    //                null
    //            }
        } catch (e: Exception) {
            Log.e(TAG, "getRandomQuote: Wyjątek podczas pobierania cytatu", e)
            null
        } as Quote?
    }
    
    // User management
    suspend fun getUserData(uid: String): User? = localUserRepository.getUserByUid(uid)
    
    suspend fun getCurrentUser(): User? = localUserRepository.getCurrentUser()
    
    suspend fun updateUserPreferences(uid: String, categories: List<String>): Boolean {
        return localUserRepository.saveUserCategories(uid, categories).isSuccess
    }
    
    suspend fun createUserFromFirebaseAuth(): Boolean {
        val firebaseUser = auth.currentUser ?: return false
        val user = User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: "",
            preferredCategories = emptyList()
        )
        return localUserRepository.saveUser(user).isSuccess
    }
    
    suspend fun signOut() {
        auth.signOut()
        localUserRepository.clearCurrentUser()
    }
    
    companion object {
        private const val TAG = "QuoteRepository"
    }
}