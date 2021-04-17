package com.example.android.mycampusapp.timetable.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import com.example.android.mycampusapp.data.CustomTime
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.timetable.receiver.TimetableAlarmReceiver
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        val requestCode = remoteMessage.data["requestCode"]
        val cancelledSubject = remoteMessage.data["cancelSubject"]
        val cancelDay = remoteMessage.data["cancelDay"]

        val updateId = remoteMessage.data["updateId"]
        val updateDay = remoteMessage.data["updateDay"]

        if (!updateDay.isNullOrEmpty() && !updateId.isNullOrEmpty()) {
            val dayOfWeek = enumValueOf<DayOfWeek>(updateDay)
            courses.document(courseId).collection(dayOfWeek.name).document(updateId).get()
                .addOnSuccessListener { updateDocument ->
                    Timber.i("update success")

                    val timetableClass = updateDocument.toObject(TimetableClass::class.java)!!
                    val intent =
                        Intent(applicationContext, TimetableAlarmReceiver::class.java).apply {
                            val message =
                                "${timetableClass.subject} is starting at ${
                                    formatTime(getTimetableCustomTime(timetableClass))
                                } in ${timetableClass.locationName} Room ${timetableClass.room}"
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
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        getTimetableCalendar(timetableClass, dayOfWeek).timeInMillis,
                        pendingIntent
                    )
                    if (dayOfWeek == getTodayEnumDay()) {
                        val immediateMessage =
                            "**TODAY** ${timetableClass.subject} will start at ${
                                formatTime(getTimetableCustomTime(timetableClass))
                            } in ${timetableClass.locationName} Room ${timetableClass.room}"
                        sendNotification(immediateMessage, getTodayEnumDay())
                    }
                    if (dayOfWeek == getTomorrowEnumDay()) {
                        val immediateMessage =
                            "**TOMORROW** ${timetableClass.subject} will start at ${
                                formatTime(getTimetableCustomTime(timetableClass))
                            } in ${timetableClass.locationName} Room ${timetableClass.room}"
                        sendNotification(immediateMessage, getTodayEnumDay())
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

    private fun sendNotification(message: String, dayOfWeek: DayOfWeek) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(message, dayOfWeek.name, applicationContext)
    }
}