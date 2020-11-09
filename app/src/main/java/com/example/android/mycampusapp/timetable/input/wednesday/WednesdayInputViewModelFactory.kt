package com.example.android.mycampusapp.timetable.input.wednesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.google.firebase.firestore.DocumentReference

class WednesdayInputViewModelFactory(
    private val courseDocument: DocumentReference,
    private val wednesdayClass: TimetableClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WednesdayInputViewModel(
            courseDocument,
            wednesdayClass,
            app
        ) as T
    }
}