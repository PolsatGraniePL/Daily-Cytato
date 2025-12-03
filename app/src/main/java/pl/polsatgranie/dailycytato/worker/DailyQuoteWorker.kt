package pl.polsatgranie.dailycytato.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.polsatgranie.dailycytato.data.repository.QuoteRepository
import pl.polsatgranie.dailycytato.utils.NotificationHelper

/**
 * Worker odpowiedzialny za codzienne pobieranie cytatu i wysyłanie powiadomienia
 */
class DailyQuoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val repository = QuoteRepository(context)
    private val notificationHelper = NotificationHelper(context)
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Pobierz preferencje użytkownika
            val currentUser = repository.getCurrentUser()
            
            if (currentUser == null) {
                // Jeśli użytkownik nie jest zalogowany, nie wysyłaj powiadomienia
                return@withContext Result.success()
            }
            
            // Pobierz cytat zgodny z preferencjami użytkownika
            val quote = repository.getRandomQuote(currentUser.preferredCategories)
            
            if (quote != null) {
                // Wyślij powiadomienie z cytatem
                notificationHelper.showDailyQuoteNotification(
                    title = "Cytat dnia",
                    content = quote.content,
                    author = quote.author
                )
                
                // Zaktualizuj widget
                updateWidget(quote)
                
                Result.success()
            } else {
                // Jeśli nie udało się pobrać cytatu, wyślij powiadomienie z domyślną wiadomością
                notificationHelper.showDailyQuoteNotification(
                    title = "Daily Cytato",
                    content = "Sprawdź aplikację, aby zobaczyć cytat dnia!",
                    author = ""
                )
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    private fun updateWidget(quote: pl.polsatgranie.dailycytato.data.model.Quote) {
        // Wyślij broadcast do widget provider
        val intent = android.content.Intent(applicationContext, 
            pl.polsatgranie.dailycytato.widget.QuoteWidgetProvider::class.java)
        intent.action = "UPDATE_WIDGET"
        intent.putExtra("quote_content", quote.content)
        intent.putExtra("quote_author", quote.author)
        applicationContext.sendBroadcast(intent)
    }
}