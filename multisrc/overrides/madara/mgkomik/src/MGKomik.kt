package eu.kanade.tachiyomi.revived.id.mgkomik

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.multisrc.madara.Madara
import eu.kanade.tachiyomi.lib.cookiemanager.PersistentCookieManager
import eu.kanade.tachiyomi.lib.cookiemanager.TokenManager
import eu.kanade.tachiyomi.lib.cookiemanager.TokenRefreshWorker
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import okhttp3.Headers
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MGKomik : Madara("MG Komik", "https://id.mgkomik.cc", "id", SimpleDateFormat("dd MMM yy", Locale.US)) {
   
    private val cookieManager = PersistentCookieManager(network.preferences)
    private val tokenManager = TokenManager(network.context)

    override val client: OkHttpClient = super.client.newBuilder()
        .rateLimit(20, 5, TimeUnit.SECONDS)
        .build()

    override fun headersBuilder(): Headers.Builder = super.headersBuilder()
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
        .add("Accept-Language", "en-US,en;q=0.9,id;q=0.8")
        .add("Sec-Fetch-Dest", "document")
        .add("Sec-Fetch-Mode", "navigate")
        .add("Sec-Fetch-Site", "same-origin")
        .add("Sec-Fetch-User", "?1")
        .add("Upgrade-Insecure-Requests", "1")
        .add("X-Requested-With", randomString)

    private fun generateRandomString(length: Int): String {
        val charset = "HALOGaES.BCDFHIJKMNPQRTUVWXYZ.bcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun searchPage(page: Int): String = if (page > 1) "page/$page/" else ""

    private val randomLength = Random.Default.nextInt(13, 21)

    private val randomString = generateRandomString(randomLength)

    override val mangaSubString = "komik"

    override fun searchMangaNextPageSelector() = "a.page.larger"

    override val chapterUrlSuffix = ""
    
    init {
        scheduleTokenRefresh()
    }
    
    private fun scheduleTokenRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
            1, TimeUnit.HOURS
        ).build()
    
        WorkManager.getInstance(network.context).enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
