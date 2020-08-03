package com.example.android.mycampusapp.timetable.display.days.wednesday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource

class WednesdayViewModelFactory(private val repository: TimetableDataSource):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WednesdayViewModel(
            repository
        ) as T
    }
}