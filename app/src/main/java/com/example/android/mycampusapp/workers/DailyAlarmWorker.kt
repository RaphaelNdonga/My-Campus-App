package com.example.android.mycampusapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.location.LocationUtils
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.getTomorrowEnumDay
import com.example.android.mycampusapp.util.sharedPrefFile
import com.google.firebase.firestore.CollectionReference
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*
import javax.inject.Inject

@HiltWorker
class DailyAlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var coursesCollection: CollectionReference

    override fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!

        val location = LocationUtils.getJkuatLocations()[0]

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val workerClass = TimetableClass(
            subject = "WorkManager",
            hour = hour,
            minute = minute,
            locationName = location.name,
            locationCoordinates = location.coordinates,
            room = "101"
        )

        addFirestoreData(workerClass, courseId)

        return Result.success()
    }

    companion object {
        const val WORKER_NAME = "DailyAlarmWorker"
    }

    private fun addFirestoreData(timetableClass: TimetableClass, courseId: String) {
        coursesCollection.document(courseId).collection(getTomorrowEnumDay().name)
            .document(timetableClass.id).set(timetableClass)
    }
}