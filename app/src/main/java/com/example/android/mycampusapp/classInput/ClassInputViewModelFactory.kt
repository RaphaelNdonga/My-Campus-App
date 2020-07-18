package com.example.android.mycampusapp.classInput

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDao
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource

class ClassInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClassInputViewModel(timetableRepository, mondayClass, app) as T
    }
}