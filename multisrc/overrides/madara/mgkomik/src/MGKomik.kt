package eu.kanade.tachiyomi.revived.id.mgkomik

import android.util.Log
import eu.kanade.tachiyomi.multisrc.madara.Madara
import eu.kanade.tachiyomi.network.interceptor.rateLimit
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MGKomik : Madara(
    "MG Komik",
    "https://id.mgkomik.cc",
    "id",
    SimpleDateFormat("dd MMM yy", Locale.US)
) {

    private val TAG = "MGKomik"

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .rateLimit(20, 5, TimeUnit.SECONDS)
        .addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            Log.d(TAG, "Response code: ${response.code}")
            response
        })
        .build()

    private fun generateRandomString(length: Int): String {
        val charset = "HALOGaES.BCDFHIJKMNPQRTUVWXYZ.bcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun randomUserAgent(): String {
        val agents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15",
            "Mozilla/5.0 (Linux; Android 11; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36"
        )
        return agents.random()
    }

    override fun headersBuilder(): Headers.Builder {
        val randomLength = Random.nextInt(13, 21)
        val randomString = generateRandomString(randomLength)
        val userAgent = randomUserAgent()

        Log.d(TAG, "Generating headers with X-Requested-With: $randomString")
        Log.d(TAG, "Using User-Agent: $userAgent")

        return super.headersBuilder()
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .add("Accept-Language", "en-US,en;q=0.9,id;q=0.8")
            .add("Sec-Fetch-Dest", "document")
            .add("Sec-Fetch-Mode", "navigate")
            .add("Sec-Fetch-Site", "same-origin")
            .add("Sec-Fetch-User", "?1")
            .add("Upgrade-Insecure-Requests", "1")
            .add("X-Requested-With", randomString)
            .add("User-Agent", userAgent)
    }

    override fun searchPage(page: Int): String {
        val path = if (page > 1) "page/$page/" else ""
        Log.d(TAG, "searchPage called with page=$page, path=$path")
        return path
    }

    override val mangaSubString = "komik"
    override fun searchMangaNextPageSelector(): String = "a.page.larger"
    override val chapterUrlSuffix = ""
}