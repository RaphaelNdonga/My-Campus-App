package com.mycampusapp.links

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class LinksViewModelFactory(private val collection: CollectionReference) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LinksViewModel(collection) as T
    }

}
