package com.example.android.mycampusapp.timetable.input.monday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.MondayClass
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource

class MondayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val mondayClass: MondayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MondayInputViewModel(
            timetableRepository,
            mondayClass,
            app
        ) as T
    }
}