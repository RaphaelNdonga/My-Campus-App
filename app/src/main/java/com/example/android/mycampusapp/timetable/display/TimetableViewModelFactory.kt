package com.example.android.mycampusapp.timetable.display

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class TimetableViewModelFactory(
    private val dayCollection: CollectionReference,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TimetableViewModel(
            dayCollection,app
        ) as T
    }
}