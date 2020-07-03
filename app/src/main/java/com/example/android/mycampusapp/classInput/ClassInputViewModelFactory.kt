package com.example.android.mycampusapp.classInput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDao
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource

class ClassInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClassInputViewModel(timetableRepository, mondayClass) as T
    }
}