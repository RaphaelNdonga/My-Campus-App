package com.mycampusapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.CollectionReference
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.getYesterdayEnumDay
import com.mycampusapp.util.sharedPrefFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class ReactivatingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    @Inject
    lateinit var courseCollection: CollectionReference
    override fun doWork(): Result {
        val sharedPreferences =
            this.applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        val yesterdayCollection =
            courseCollection.document(courseId).collection(getYesterdayEnumDay().name)
        /**
         * Yesterday collection is used because this worker is initialized by today's classes.
         * After initialization, a time delay of 1 day is set. By the time this worker does its
         * work, it shall be tomorrow. And never later or sooner.
         */
        yesterdayCollection.get().addOnSuccessListener {
            val yesterdayClassList = it.toObjects(TimetableClass::class.java)
            yesterdayClassList.forEach { timetableClass ->
                yesterdayCollection.document(timetableClass.id).update("active", true)
            }
        }
        return Result.success()
    }
    companion object {
        const val WORKER_NAME = "ReactivatingWorker"
    }
}