package pl.polsatgranie.dailycytato.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import pl.polsatgranie.dailycytato.MainActivity
import pl.polsatgranie.dailycytato.R
import pl.polsatgranie.dailycytato.worker.DailyQuoteWorker

/**
 * Widget provider dla wyświetlania cytatu na ekranie głównym
 */
class QuoteWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Aktualizuj wszystkie instancje widgetu
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            "UPDATE_WIDGET" -> {
                val quoteContent = intent.getStringExtra("quote_content") ?: ""
                val quoteAuthor = intent.getStringExtra("quote_author") ?: ""
                updateWidgetWithQuote(context, quoteContent, quoteAuthor)
            }
            "REFRESH_QUOTE" -> {
                // Uruchom worker do pobrania nowego cytatu
                val workRequest = OneTimeWorkRequestBuilder<DailyQuoteWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Pobierz zapisany cytat z SharedPreferences
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val quoteContent = prefs.getString("last_quote_content", "Dotknij aby załadować cytat dnia")
        val quoteAuthor = prefs.getString("last_quote_author", "")
        
        val views = RemoteViews(context.packageName, R.layout.widget_quote)
        
        // Ustaw tekst cytatu
        views.setTextViewText(R.id.widget_quote_content, "\"$quoteContent\"")
        views.setTextViewText(R.id.widget_quote_author, 
            if (quoteAuthor?.isNotEmpty() == true) "— $quoteAuthor" else "")
        
        // Dodaj akcję kliknięcia - otwórz aplikację
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)
        
        // Dodaj akcję odświeżania
        val refreshIntent = Intent(context, QuoteWidgetProvider::class.java)
        refreshIntent.action = "REFRESH_QUOTE"
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context, 1, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)
        
        // Zaktualizuj widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun updateWidgetWithQuote(context: Context, quoteContent: String, quoteAuthor: String) {
        // Zapisz cytat w SharedPreferences
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("last_quote_content", quoteContent)
            .putString("last_quote_author", quoteAuthor)
            .apply()
        
        // Zaktualizuj wszystkie instancje widgetu
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            android.content.ComponentName(context, QuoteWidgetProvider::class.java)
        )
        
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}