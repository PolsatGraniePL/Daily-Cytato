package pl.polsatgranie.dailycytato.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Model dla cytatu otrzymanego z API Quotify
 * API zwraca pole "text" dla treści cytatu
 */
@JsonClass(generateAdapter = true)
data class Quote(
    @Json(name = "text")
    val content: String,
    
    @Json(name = "author")
    val author: String,
    
    @Json(name = "source")
    val source: String? = null,
    
    @Json(name = "tags")
    val tags: List<String>? = null
)

/**
 * Model dla odpowiedzi z API Quotify
 * API zwraca listę cytatów, więc używamy tego samego modelu co Quote
 */
typealias QuoteResponse = Quote