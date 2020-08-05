package com.example.android.mycampusapp.timetable.input.sunday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.SundayClass
import com.google.firebase.firestore.FirebaseFirestore

class SundayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val sundayClass: SundayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SundayInputViewModel(
            firestore,
            sundayClass,
            app
        ) as T
    }
}