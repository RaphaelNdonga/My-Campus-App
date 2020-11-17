package com.example.android.mycampusapp.timetable.input.monday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.TimetableClass
import com.google.firebase.firestore.DocumentReference

class MondayInputViewModelFactory(
    private val coursesDocumentReference: DocumentReference,
    private val mondayClass: TimetableClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MondayInputViewModel(
            coursesDocumentReference,
            mondayClass,
            app
        ) as T
    }
}