package com.mycampusapp.timetable.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mycampusapp.assessments.AssessmentType
import com.mycampusapp.data.Assessment
import com.mycampusapp.data.CustomDate
import com.mycampusapp.data.CustomTime
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.mycampusapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var courses: CollectionReference


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sharedPreferences =
            applicationContext.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!

        val settingsPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val minutesPrior = settingsPreference.getString("prior_alarm", "0")!!.toLong()

        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        val requestCode = remoteMessage.data["requestCode"]
        val cancelledSubject = remoteMessage.data["cancelSubject"]
        val cancelDay = remoteMessage.data["cancelDay"]

        val updateId = remoteMessage.data["updateId"]
        val updateDay = remoteMessage.data["updateDay"]

        val updateAssessmentId = remoteMessage.data["updateAssessmentId"]
        val updateAssessmentType = remoteMessage.data["updateAssessmentType"]

        val cancelRequestCode = remoteMessage.data["assessmentRequestCode"]
        val cancelAssessmentSubject = remoteMessage.data["assessmentSubject"]
        val cancelAssessmentType = remoteMessage.data["assessmentType"]

        if (!updateDay.isNullOrEmpty() && !updateId.isNullOrEmpty()) {
            val dayOfWeek = enumValueOf<DayOfWeek>(updateDay)
            courses.document(courseId).collection(dayOfWeek.name).document(updateId).get()
                .addOnSuccessListener { updateDocument ->
                    Timber.i("update success")

                    val timetableClass = updateDocument.toObject(TimetableClass::class.java)
                    timetableClass?.let {
                        val intent =
                            Intent(applicationContext, TimetableAlarmReceiver::class.java).apply {
                                val message =
                                    "${timetableClass.subject} is starting at ${
                                        formatTime(getTimetableCustomTime(timetableClass))
                                    } in ${timetableClass.locationNameOrLink} Room ${timetableClass.room}"
                                putExtra("message", message)
                                putExtra("dayOfWeek", dayOfWeek.name)
                            }
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext,
                            timetableClass.alarmRequestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val alarmManager =
                            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                        val triggerTime =
                            getTimetableCalendar(
                                timetableClass,
                                dayOfWeek
                            ).timeInMillis - millisPrior
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )
                        } else {
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )
                        }
                        Timber.i("The alarm will ring ${TimeUnit.MILLISECONDS.toMinutes(triggerTime - System.currentTimeMillis())} minutes from now")
                        if (dayOfWeek == getTodayEnumDay()) {
                            val immediateMessage =
                                "**TODAY** ${timetableClass.subject} will start at ${
                                    formatTime(getTimetableCustomTime(timetableClass))
                                } in ${timetableClass.locationNameOrLink} Room ${timetableClass.room}"
                            sendNotification(immediateMessage, dayOfWeek)
                        }
                        if (dayOfWeek == getTomorrowEnumDay()) {
                            val immediateMessage =
                                "**TOMORROW** ${timetableClass.subject} will start at ${
                                    formatTime(getTimetableCustomTime(timetableClass))
                                } in ${timetableClass.locationNameOrLink} Room ${timetableClass.room}"
                            sendNotification(immediateMessage, dayOfWeek)
                        }
                    }
                }
        }

        if (!updateAssessmentId.isNullOrEmpty() && !updateAssessmentType.isNullOrEmpty()) {
            val assessmentType = enumValueOf<AssessmentType>(updateAssessmentType)
            courses.document(courseId).collection(assessmentType.name)
                .document(updateAssessmentId)
                .get().addOnSuccessListener {
                    val assessment = it.toObject(Assessment::class.java)
                    assessment?.let {
                        val message = "${assessment.subject} ${
                            assessmentType.name.lowercase(
                                Locale.ROOT
                            )
                        } has been set to be collected on ${
                            formatDate(
                                CustomDate(assessment.year, assessment.month, assessment.day)
                            )
                        } at ${
                            formatTime(
                                CustomTime(assessment.hour, assessment.minute)
                            )
                        }"
                        val intent = Intent(this, TimetableAlarmReceiver::class.java).apply {
                            putExtra("message", message)
                            putExtra("assessmentType", assessmentType.name)
                        }

                        val pendingIntent = PendingIntent.getBroadcast(
                            this,
                            assessment.alarmRequestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        val alarmManager =
                            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val millisPrior = TimeUnit.MINUTES.toMillis(minutesPrior)
                        val triggerTime =
                            getAssessmentCalendar(assessment).timeInMillis - millisPrior
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )
                        } else {
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )
                        }
                        sendNotification(message = message, assessmentType = assessmentType)
                    }
                }
        }

        if (!requestCode.isNullOrEmpty() && !cancelDay.isNullOrEmpty() && !cancelledSubject.isNullOrEmpty()) {
            Timber.i("Cancel today success")
            val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                requestCode.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            val dayOfWeek = enumValueOf<DayOfWeek>(cancelDay)

            if (dayOfWeek == getTodayEnumDay()) {
                val notificationMessage =
                    "**TODAY** $cancelledSubject will not be happening"
                sendNotification(notificationMessage, dayOfWeek)
            }

            if (dayOfWeek == getTomorrowEnumDay()) {
                val notificationMessage =
                    "**TOMORROW** $cancelledSubject will not be happening"
                sendNotification(notificationMessage, dayOfWeek)
            }

            Timber.i("The cancel alarm id is $requestCode")
        }
        if (!cancelRequestCode.isNullOrEmpty() && !cancelAssessmentSubject.isNullOrEmpty() && !cancelAssessmentType.isNullOrEmpty()) {
            val intent = Intent(this, TimetableAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                cancelRequestCode.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)

            val assessmentType = enumValueOf<AssessmentType>(cancelAssessmentType)
            val notificationMessage =
                "$cancelAssessmentSubject ${assessmentType.name.lowercase(Locale.ROOT)} will not be happening"

            sendNotification(message = notificationMessage, assessmentType = assessmentType)
        }
    }

    private fun formatTime(timetableCustomTime: CustomTime): String {
        return if (DateFormat.is24HourFormat(applicationContext)) {
            format24HourTime(timetableCustomTime)
        } else {
            formatAmPmTime(timetableCustomTime)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("A new token has been received $token")
    }

    private fun sendNotification(
        message: String,
        dayOfWeek: DayOfWeek? = null,
        assessmentType: AssessmentType? = null
    ) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
            message = message,
            dayOfWeekString = dayOfWeek?.name,
            assessmentTypeString = assessmentType?.name,
            context = applicationContext
        )
    }
}