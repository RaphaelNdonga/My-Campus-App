package com.example.android.mycampusapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.android.mycampusapp.workers.DailyAlarmWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivityViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)


    fun setupRecurringWork() {
        applicationScope.launch {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyAlarmWorker>(
                1, TimeUnit.DAYS
            ).setConstraints(constraints).build()

            WorkManager.getInstance(app).enqueueUniquePeriodicWork(
                DailyAlarmWorker.WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }

}
