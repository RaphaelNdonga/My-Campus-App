package com.mycampusapp.workers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.hilt.work.HiltWorker
import androidx.preference.PreferenceManager
import androidx.work.*
import com.google.firebase.firestore.CollectionReference
import com.mycampusapp.assessments.AssessmentType
import com.mycampusapp.data.Assessment
import com.mycampusapp.data.CustomTime
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.mycampusapp.util.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit
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

        val settingsPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val minutesPrior = settingsPreference.getString("prior_alarm", "0")!!.toLong()

        /**
         * Why do we set two alarms?
         * Let's suppose the user installs the app at 9:00 pm.
         * The daily alarm will get today's classes, but since there are none, no alarms will be
         * set. Tomorrow, the worker will work at exactly 9:00 pm again, or thereabouts.
         * Therefore there will be an infinite loop of alarms not set if the alarm sets today's
         * classes.
         * That's why we set tomorrow's alarms also.
         * There is no overhead cost for overlaps in the alarm manager
         */

        val tomorrowCollection = getTomorrowEnumDay().name
        val todayCollection = getTodayEnumDay().name

        /**
         * Why do we need ALARM_SET_COLLECTION?
         * Let's say we use getTomorrowEnumDay.name to set the alarm and also to clear it through
         * the clearance worker.
         * If the alarm is set today and the clearance is done tomorrow before the next worker runs,
         * what happens?
         * The clearance will use tomorrow's collection instead of today's.
         * Therefore, it is necessary to keep track of which collection has been set by the
         * worker, mainly for clearance purposes. We don't want any leftover collections lying
         * around.
         */
        sharedPreferences.edit().putStringSet(
            ALARM_SET_COLLECTION, setOf(todayCollection, tomorrowCollection)
        ).apply()

        val todayClasses = coursesCollection.document(courseId).collection(todayCollection).get()

        todayClasses.addOnSuccessListener {
            Timber.i("In today's loop")
            it.forEach { documentSnapshot ->
                val todayClass = documentSnapshot.toObject(TimetableClass::class.java)
                if (timetableClassIsLater(todayClass) && todayClass.isActive) {
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                    val message = "${todayClass.subject} starts at ${
                        formatTime(
                            CustomTime(
                                todayClass.hour,
                                todayClass.minute
                            )
                        )
                    } in ${todayClass.locationNameOrLink} room ${todayClass.room}"
                    intent.putExtra("message", message)
                    intent.putExtra("dayOfWeek", getTodayEnumDay().name)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext,
                        todayClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                    val triggerTime = getTimetableCalendar(todayClass, getTodayEnumDay())
                        .timeInMillis - millisPrior
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
                /**
                 * Only set up the reactivating worker if there is an inactive class. Even though
                 * this function might be called repeatedly, ExistingWorkPolicy.KEEP will sort
                 * that out.
                 */
                if(todayClass.isActive.not()){
                    setUpReactivatingWorker()
                }
            }
        }

        val tomorrowTimetableClasses =
            coursesCollection.document(courseId).collection(tomorrowCollection).get()

        tomorrowTimetableClasses.addOnSuccessListener {
            Timber.i("In tomorrow's loop")
            it.forEach { documentSnapshot ->
                val tomorrowClass = documentSnapshot.toObject(TimetableClass::class.java)
                if (tomorrowClass.isActive) {
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                    val message = "${tomorrowClass.subject} starts at ${
                        formatTime(
                            CustomTime(
                                tomorrowClass.hour,
                                tomorrowClass.minute
                            )
                        )
                    } in ${tomorrowClass.locationNameOrLink} room ${tomorrowClass.room}"
                    intent.putExtra("message", message)
                    intent.putExtra("dayOfWeek", getTomorrowEnumDay().name)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext,
                        tomorrowClass.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val tomorrowCalendar = getTomorrowTimetableCalendar(tomorrowClass)

                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                    val triggerTime = tomorrowCalendar.timeInMillis - millisPrior
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        }

        val assignmentsCollection = AssessmentType.ASSIGNMENT.name
        val assessmentQuerySnapshot =
            coursesCollection.document(courseId).collection(assignmentsCollection).get()

        assessmentQuerySnapshot.addOnSuccessListener {
            it.forEach { documentSnapshot ->
                val assignment = documentSnapshot.toObject(Assessment::class.java)
                val message = "${assignment.subject} starts at ${
                    formatTime(
                        CustomTime(
                            assignment.hour,
                            assignment.minute
                        )
                    )
                } in ${assignment.locationName} room ${assignment.room}"
                if (assessmentIsLater(assignment)) {
                    Timber.i("We're in the loop")
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                    intent.putExtra("assessmentType", assignmentsCollection)
                    intent.putExtra("message", message)
                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext,
                        assignment.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                    val triggerTime = getAssessmentCalendar(assignment).timeInMillis - millisPrior
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        }

        val testsCollection = AssessmentType.TEST.name

        val testQuerySnapshot =
            coursesCollection.document(courseId).collection(testsCollection).get()

        testQuerySnapshot.addOnSuccessListener {
            it.forEach { documentSnapshot ->
                val test = documentSnapshot.toObject(Assessment::class.java)
                val message = "${test.subject} starts at ${
                    formatTime(
                        CustomTime(
                            test.hour,
                            test.minute
                        )
                    )
                } in ${test.locationName} room ${test.room}"
                if (assessmentIsLater(test)) {
                    Timber.i("We're in the loop")
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
                    intent.putExtra("assessmentType", testsCollection)
                    intent.putExtra("message", message)

                    val pendingIntent = PendingIntent.getBroadcast(
                        applicationContext,
                        test.alarmRequestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                    val triggerTime = getAssessmentCalendar(test).timeInMillis - millisPrior
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        }


        return Result.success()
    }

    private fun setUpReactivatingWorker() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        /**
         * The worker has a time delay of one day because?
         * If the alarms have been set to ring today, we don't want the reactivation to happen today.
         * This is because, it is a bad user experience to observe that the skipped class has
         * reactivated itself before its time has passed.
         * Therefore, all reactivation happens the following day.
         */

        val oneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ReactivatingWorker>().setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.DAYS).build()

        WorkManager.getInstance(this.applicationContext)
            .enqueueUniqueWork(
                ReactivatingWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                oneTimeWorkRequest
            )
    }

    private fun formatTime(customTime: CustomTime): String {
        return if (DateFormat.is24HourFormat(applicationContext)) {
            format24HourTime(customTime)
        } else {
            formatAmPmTime(customTime)
        }
    }

    companion object {
        const val WORKER_NAME = "DailyAlarmWorker"
    }
}