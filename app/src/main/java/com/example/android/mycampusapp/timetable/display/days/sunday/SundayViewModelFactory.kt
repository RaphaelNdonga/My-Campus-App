package com.example.android.mycampusapp.timetable.display.days.sunday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class SundayViewModelFactory(private val courseDocument: DocumentReference,private val app:Application):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SundayViewModel(
            courseDocument,app
        ) as T
    }
}