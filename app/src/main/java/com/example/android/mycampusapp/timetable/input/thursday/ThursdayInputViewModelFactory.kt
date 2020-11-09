package com.example.android.mycampusapp.timetable.input.thursday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.google.firebase.firestore.DocumentReference

class ThursdayInputViewModelFactory(
    private val courseDocument: DocumentReference,
    private val thursdayClass: TimetableClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ThursdayInputViewModel(
            courseDocument,
            thursdayClass,
            app
        ) as T
    }
}