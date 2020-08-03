package com.example.android.mycampusapp.timetable.input.friday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.FridayClass
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource

class FridayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val fridayClass: FridayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FridayInputViewModel(
            timetableRepository,
            fridayClass,
            app
        ) as T
    }
}