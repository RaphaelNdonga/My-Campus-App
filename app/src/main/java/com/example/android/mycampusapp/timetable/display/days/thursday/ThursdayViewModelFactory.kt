package com.example.android.mycampusapp.timetable.display.days.thursday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource

class ThursdayViewModelFactory(private val repository: TimetableDataSource):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ThursdayViewModel(
            repository
        ) as T
    }
}