package com.example.android.mycampusapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import javax.inject.Inject

@HiltWorker
class OneTimeAlarmWorker @Inject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return Result.success()
    }
}