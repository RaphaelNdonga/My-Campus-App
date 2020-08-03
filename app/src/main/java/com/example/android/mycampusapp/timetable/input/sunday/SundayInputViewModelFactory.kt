package com.example.android.mycampusapp.timetable.input.sunday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.SundayClass
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource

class SundayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val sundayClass: SundayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SundayInputViewModel(
            timetableRepository,
            sundayClass,
            app
        ) as T
    }
}