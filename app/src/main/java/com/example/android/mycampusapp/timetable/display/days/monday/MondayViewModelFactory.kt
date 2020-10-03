package com.example.android.mycampusapp.timetable.display.days.monday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class MondayViewModelFactory(private val coursesDocument: DocumentReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MondayViewModel(
            coursesDocument
        ) as T
    }
}