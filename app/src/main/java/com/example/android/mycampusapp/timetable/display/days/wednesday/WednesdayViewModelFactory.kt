package com.example.android.mycampusapp.timetable.display.days.wednesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class WednesdayViewModelFactory(private val courseDocument: DocumentReference,private val app: Application):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WednesdayViewModel(
            courseDocument,app
        ) as T
    }
}