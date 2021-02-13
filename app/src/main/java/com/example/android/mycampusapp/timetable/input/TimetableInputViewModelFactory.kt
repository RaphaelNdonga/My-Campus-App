package com.example.android.mycampusapp.timetable.input

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.TimetableClass
import com.google.firebase.firestore.CollectionReference

class TimetableInputViewModelFactory(
    private val fridayClass: TimetableClass?,
    private val app:Application,
    private val dayCollection: CollectionReference
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimetableInputViewModel(
            fridayClass,
            app,
            dayCollection
        ) as T
    }
}