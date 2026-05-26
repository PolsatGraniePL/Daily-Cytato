//package pl.polsatgranie.dailycytato.data.model
//
//import com.squareup.moshi.Json
//import com.squareup.moshi.JsonClass
//
///**
// * Model dla cytatu otrzymanego z API Quotify
// * API zwraca pole "text" dla treści cytatu
// */
//@JsonClass(generateAdapter = true)
//data class Quote(
//    @Json(name = "text")
//    val content: String,
//
//    @Json(name = "author")
//    val author: String,
//
//    @Json(name = "source")
//    val source: String? = null,
//
//    @Json(name = "tags")
//    val tags: List<String>? = null
//)
//
///**
// * Model dla odpowiedzi z API Quotify
// * API zwraca listę cytatów, więc używamy tego samego modelu co Quote
// */
//typealias QuoteResponse = Quote


package pl.polsatgranie.dailycytato.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quote(
    val id: Int,

    @Json(name = "quote")
    val content: String,

    val author: String,

    val source: String? = null,

    val tags: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "quotes")
    val quotes: List<Quote>
)
