package com.example.android.mycampusapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.util.COURSE_ID
import com.example.android.mycampusapp.util.getEnumDay
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
    private val classList = mutableListOf<TimetableClass>()

    override fun doWork(): Result {
        val sharedPreferences =
            applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timetableQuerySnapshot =
            coursesCollection.document(courseId).collection(getEnumDay(today.plus(1)).name).get()

        timetableQuerySnapshot.addOnSuccessListener { timetableClasses ->
            timetableClasses.forEach { timetableClass ->
                classList.add(timetableClass.toObject(TimetableClass::class.java))
            }
        }
        return Result.success()
    }
}