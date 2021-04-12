package com.example.android.mycampusapp.timetable.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
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

        val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        val todayTimetableId = remoteMessage.data["todayTimetableId"]
        val tomorrowTimetableId = remoteMessage.data["tomorrowTimetableId"]
        val cancelTodayId = remoteMessage.data["cancelTodayId"]
        val cancelTomorrowId = remoteMessage.data["cancelTomorrowId"]

        cancelTodayId?.let {
            Timber.i("Today was cancelled with id $it")
        }

        cancelTomorrowId?.let {
            Timber.i("Tomorrow was cancelled with id $it")
        }


        todayTimetableId?.let { timetableClassId ->

            courses.document(courseId).collection(getTodayEnumDay().name)
                .document(timetableClassId).get().addOnSuccessListener { todayDocument ->
                    Timber.i("today success")

                    val todayClass = todayDocument.toObject(TimetableClass::class.java)
                    todayClass?.let { timetableClass ->
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext,
                            timetableClass.alarmRequestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            getTodayTimetableCalendar(timetableClass).timeInMillis,
                            pendingIntent
                        )
                        val notificationMessage =
                            "**TODAY** ${timetableClass.subject} will start at ${
                                formatTime(getTimetableCustomTime(timetableClass))
                            } in ${timetableClass.locationName} Room ${timetableClass.room}"
                        sendNotification(notificationMessage, getTodayEnumDay())
                    }

                }
            Timber.i("The today's timetable class id is $timetableClassId")
        }

        tomorrowTimetableId?.let { timetableId ->
            courses.document(courseId).collection(getTomorrowEnumDay().name)
                .document(timetableId).get().addOnSuccessListener { tomorrowDocument ->
                    Timber.i("Tomorrow success")
                    val tomorrowClass = tomorrowDocument.toObject(TimetableClass::class.java)
                    tomorrowClass?.let { timetableClass ->
                        val pendingIntent = PendingIntent.getBroadcast(
                            applicationContext,
                            timetableClass.alarmRequestCode,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            getTomorrowTimetableCalendar(timetableClass).timeInMillis,
                            pendingIntent
                        )
                        val notificationMessage =
                            "**TODAY** ${timetableClass.subject} will start at ${
                                formatTime(getTimetableCustomTime(timetableClass))
                            } in ${timetableClass.locationName} Room ${timetableClass.room}"
                        sendNotification(notificationMessage, getTodayEnumDay())
                    }
                }

            Timber.i("Tomorrow's timetable class id is $timetableId")
        }
//        cancelAlarmId?.let { requestCode ->
//            val pendingIntent = PendingIntent.getBroadcast(
//                applicationContext,
//                requestCode.toInt(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//            alarmManager.cancel(pendingIntent)
//            sendNotification("cancelAlarmMessage", getTodayEnumDay())
//            Timber.i("The cancel alarm id is ${requestCode.toInt()}")
//        }
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
        notificationManager.sendNotification(message, dayOfWeek, applicationContext)
    }
}