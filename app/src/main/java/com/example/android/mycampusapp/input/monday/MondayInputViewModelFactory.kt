package com.example.android.mycampusapp.input.monday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.receiver.AlarmReceiver

class MondayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?,
    private val app:Application,
    private val alarmReceiver: AlarmReceiver
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MondayInputViewModel(timetableRepository, mondayClass, app, alarmReceiver) as T
    }
}