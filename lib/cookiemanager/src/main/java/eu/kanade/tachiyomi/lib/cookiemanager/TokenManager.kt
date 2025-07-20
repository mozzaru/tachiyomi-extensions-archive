package eu.kanade.tachiyomi.lib.cookiemanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class TokenManager(private val context: Context) {

    fun scheduleTokenRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TokenRefresh",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
