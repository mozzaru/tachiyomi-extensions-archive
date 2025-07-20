package eu.kanade.tachiyomi.lib.cookiemanager

import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.json.JSONArray

class PersistentCookieManager(private val preferences: SharedPreferences) {

    fun saveCookies(url: String, cookies: List<Cookie>) {
        val json = JSONArray()
        cookies.forEach {
            val obj = org.json.JSONObject().apply {
                put("name", it.name)
                put("value", it.value)
                put("domain", it.domain)
                put("path", it.path)
                put("expiresAt", System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)
                put("secure", it.secure)
                put("httpOnly", it.httpOnly)
            }
            json.put(obj)
        }
        preferences.edit().putString("cookies_$url", json.toString()).apply()
    }

    fun loadCookies(url: String): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        val jsonStr = preferences.getString("cookies_$url", null) ?: return cookies
        val jsonArray = JSONArray(jsonStr)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val expiresAt = obj.getLong("expiresAt")
            if (System.currentTimeMillis() > expiresAt) continue
            cookies.add(
                Builder()
                    .name(obj.getString("name"))
                    .value(obj.getString("value"))
                    .domain(obj.getString("domain"))
                    .path(obj.getString("path"))
                    .expiresAt(expiresAt)
                    .apply {
                        if (obj.getBoolean("secure")) secure()
                        if (obj.getBoolean("httpOnly")) httpOnly()
                    }
                    .build()
            )
        }
        return cookies
    }
}
