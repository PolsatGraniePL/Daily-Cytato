package pl.polsatgranie.dailycytato.data.api

import pl.polsatgranie.dailycytato.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfejs API dla serwisu Quotify
 * Dokumentacja: https://quotify.top/
 */
interface QuoteApiService {
    
    /**
     * Pobiera losowy cytat
     * API zwraca pojedynczy obiekt JSON
     * {
     *   "quotes": [
     *     {
     *       "id": 1,
     *       "quote": "Your heart is the size of an ocean. Go find yourself in its hidden depths.",
     *       "author": "Rumi"
     *     }
     * }
     *
     */
//    @GET("random")
//    suspend fun getRandomQuote(): Response<QuoteResponse>
    @GET("quotes")
    suspend fun getRandomQuote(): Response<QuoteResponse>
    
    /**
     * Pobiera cytaty według tagu
     * @param tag Tag do wyszukania
     * API zwraca tablicę cytatów
     */
    @GET("by_tag/{tag}")
    suspend fun getQuotesByTag(
        @Path("tag") tag: String
    ): Response<List<QuoteResponse>>
    
    /**
     * Pobiera cytaty według autora
     * @param author Autor do wyszukania
     * API zwraca tablicę cytatów
     */
    @GET("by_author/{author}")
    suspend fun getQuotesByAuthor(
        @Path("author") author: String
    ): Response<List<QuoteResponse>>
    
    /**
     * Wyszukuje cytaty po słowie kluczowym
     * @param query Słowo kluczowe
     * API zwraca tablicę cytatów
     */
    @GET("search")
    suspend fun searchQuotes(
        @Query("q") query: String
    ): Response<List<QuoteResponse>>
    
    /**
     * Pobiera wszystkie dostępne tagi
     */
    @GET("tags")
    suspend fun getAvailableTags(): Response<List<String>>
    
    /**
     * Pobiera wszystkich dostępnych autorów
     */
    @GET("authors")
    suspend fun getAvailableAuthors(): Response<List<String>>
    
    companion object {
//        const val BASE_URL = "https://api.quotify.top/"
        const val BASE_URL = "https://dummyjson.com/"
    }
}