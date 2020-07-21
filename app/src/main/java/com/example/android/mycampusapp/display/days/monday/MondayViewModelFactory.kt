package com.example.android.mycampusapp.display.days.monday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class MondayViewModelFactory(private val repository:TimetableDataSource):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MondayViewModel(repository) as T
    }
}