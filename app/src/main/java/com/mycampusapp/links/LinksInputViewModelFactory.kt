package com.mycampusapp.links

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference

class LinksInputViewModelFactory(private val collection: CollectionReference) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LinksInputViewModel(collection) as T
    }

}
