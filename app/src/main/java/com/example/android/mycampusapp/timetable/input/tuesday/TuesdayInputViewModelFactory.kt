package com.example.android.mycampusapp.timetable.input.tuesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.TuesdayClass
import com.google.firebase.firestore.DocumentReference

class TuesdayInputViewModelFactory(
    private val courseDocument: DocumentReference,
    private val tuesdayClass: TuesdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TuesdayInputViewModel(
            courseDocument,
            tuesdayClass,
            app
        ) as T
    }
}