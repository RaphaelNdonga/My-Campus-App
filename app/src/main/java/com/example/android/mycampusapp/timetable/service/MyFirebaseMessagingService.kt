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

        Timber.i("A new message has been received from ${remoteMessage.from}")
        Timber.i("The message is ${remoteMessage.data}")
        val todayTimetableId = remoteMessage.data["todayTimetableId"]
        val tomorrowTimetableId = remoteMessage.data["tomorrowTimetableId"]
        val todayRequestCode = remoteMessage.data["todayRequestCode"]
        val tomorrowRequestCode = remoteMessage.data["tomorrowRequestCode"]
        val todayCancelledSubject = remoteMessage.data["todayCancelledSubject"]
        val tomorrowCancelledSubject = remoteMessage.data["tomorrowCancelledSubject"]


        todayTimetableId?.let { timetableClassId ->
            courses.document(courseId).collection(getTodayEnumDay().name)
                .document(timetableClassId).get().addOnSuccessListener { todayDocument ->
                    Timber.i("today success")

                    val timetableClass = todayDocument.toObject(TimetableClass::class.java)!!
                    val intent =
                        Intent(applicationContext, TimetableAlarmReceiver::class.java).also {
                            val alarmMessage =
                                "${timetableClass.subject} is starting at ${
                                    formatTime(getTimetableCustomTime(timetableClass))
                                } in ${timetableClass.locationName} Room ${timetableClass.room}"

                            it.putExtra("message", alarmMessage)
                            Timber.i("The extra message is $alarmMessage")

                            it.putExtra("dayOfWeek", getTodayEnumDay().name)
                            Timber.i("The extra dayOfWeek is ${getTodayEnumDay().name}")
                        }
                    Timber.i("The intent is $intent")

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
                        getTodayTimetableCalendar(timetableClass).timeInMillis,
                        pendingIntent
                    )
                    val immediateMessage =
                        "**TODAY** ${timetableClass.subject} will start at ${
                            formatTime(getTimetableCustomTime(timetableClass))
                        } in ${timetableClass.locationName} Room ${timetableClass.room}"
                    sendNotification(immediateMessage, getTodayEnumDay())
                }
            Timber.i("The today's timetable class id is $timetableClassId")
        }

        tomorrowTimetableId?.let { timetableId ->
            courses.document(courseId).collection(getTomorrowEnumDay().name)
                .document(timetableId).get().addOnSuccessListener { tomorrowDocument ->
                    Timber.i("Tomorrow success")
                    val timetableClass = tomorrowDocument.toObject(TimetableClass::class.java)!!
                    val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
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
                        getTomorrowTimetableCalendar(timetableClass).timeInMillis,
                        pendingIntent
                    )
                    val alarmMessage =
                        "${timetableClass.subject} is starting at ${
                            formatTime(getTimetableCustomTime(timetableClass))
                        } in ${timetableClass.locationName} Room ${timetableClass.room}"
                    intent.putExtra("message", alarmMessage)
                    intent.putExtra("dayOfWeek", getTomorrowEnumDay().name)

                    val immediateMessage =
                        "**TODAY** ${timetableClass.subject} will start at ${
                            formatTime(getTimetableCustomTime(timetableClass))
                        } in ${timetableClass.locationName} Room ${timetableClass.room}"
                    sendNotification(immediateMessage, getTodayEnumDay())
                }

            Timber.i("Tomorrow's timetable class id is $timetableId")
        }
        todayRequestCode?.let { requestCodeString ->
            Timber.i("Cancel today success")
            val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                requestCodeString.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            val notificationMessage =
                "**TODAY** $todayCancelledSubject will not be happening"
            sendNotification(notificationMessage, getTodayEnumDay())
            Timber.i("The cancel alarm id is $requestCodeString")
        }
        tomorrowRequestCode?.let { requestCodeString ->
            val intent = Intent(applicationContext, TimetableAlarmReceiver::class.java)
            Timber.i("Cancel tomorrow success")
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                requestCodeString.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager =
                applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            val notificationMessage =
                "**TOMORROW** $tomorrowCancelledSubject will not be happening"
            sendNotification(notificationMessage, getTomorrowEnumDay())
            Timber.i("The cancel alarm id is $requestCodeString")
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