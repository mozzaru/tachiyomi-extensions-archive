package eu.kanade.tachiyomi.revived.id.mgkomik

import android.util.Log
import eu.kanade.tachiyomi.multisrc.madara.Madara
import okhttp3.Headers
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MGKomik : Madara(
    "MG Komik",
    "https://id.mgkomik.cc",
    "id",
    SimpleDateFormat("dd MMM yy", Locale.US)
) {

    private val TAG = "MGKomik"

    override val client = network.cloudflareClient.newBuilder()
        .rateLimit(20, 5, TimeUnit.SECONDS)
        .build().also {
            Log.d(TAG, "OkHttpClient initialized with rate limit 20 per 5 seconds")
        }

    override fun headersBuilder(): Headers.Builder {
        val userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

        Log.d(TAG, "Generating headers with User-Agent: $userAgent")

        return Headers.Builder()
            .add("User-Agent", userAgent)
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .add("Accept-Language", "en-US,en;q=0.9,id;q=0.8")
    }

    override fun searchPage(page: Int): String {
        val path = if (page > 1) "page/$page/" else ""
        Log.d(TAG, "searchPage called with page=$page, path=$path")
        return path
    }

    override fun searchMangaNextPageSelector(): String {
        Log.d(TAG, "Next page selector requested")
        return "a.page.larger"
    }

    override val mangaSubString = "komik"

    override val chapterUrlSuffix = ""
}