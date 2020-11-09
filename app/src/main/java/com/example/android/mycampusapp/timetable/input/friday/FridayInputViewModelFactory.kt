package com.example.android.mycampusapp.timetable.input.friday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.TimetableClass
import com.google.firebase.firestore.DocumentReference

class FridayInputViewModelFactory(
    private val fridayClass: TimetableClass?,
    private val app:Application,
    private val courseDocument: DocumentReference
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FridayInputViewModel(
            fridayClass,
            app,
            courseDocument
        ) as T
    }
}