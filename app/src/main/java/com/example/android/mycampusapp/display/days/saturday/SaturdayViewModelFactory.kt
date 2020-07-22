package com.example.android.mycampusapp.display.days.saturday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class SaturdayViewModelFactory(private val repository:TimetableDataSource):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SaturdayViewModel(repository) as T
    }
}